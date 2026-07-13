package com.keyloop.documentviewer.api

import com.keyloop.documentviewer.domain.Vin
import com.keyloop.documentviewer.service.DocumentAggregationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/vehicles")
class DocumentSearchController(
    private val aggregationService: DocumentAggregationService,
) {
    @Operation(
        summary = "Find all Sales and Service documents for a VIN",
        parameters =
            [
                Parameter(
                    name = CorrelationIdFilter.HEADER_NAME,
                    `in` = ParameterIn.HEADER,
                    required = false,
                    description = "Optional UUID; generated when absent or invalid",
                    schema = Schema(type = "string", format = "uuid"),
                ),
            ],
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Complete or partial document result",
                    headers =
                        [
                            Header(
                                name = CorrelationIdFilter.HEADER_NAME,
                                schema = Schema(type = "string", format = "uuid"),
                            ),
                        ],
                    content = [Content(schema = Schema(implementation = DocumentSearchResponse::class))],
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Malformed VIN",
                    headers =
                        [
                            Header(
                                name = CorrelationIdFilter.HEADER_NAME,
                                schema = Schema(type = "string", format = "uuid"),
                            ),
                        ],
                    content =
                        [
                            Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = Schema(implementation = ProblemDetail::class),
                            ),
                        ],
                ),
                ApiResponse(
                    responseCode = "503",
                    description = "Neither source is usable",
                    headers =
                        [
                            Header(
                                name = CorrelationIdFilter.HEADER_NAME,
                                schema = Schema(type = "string", format = "uuid"),
                            ),
                        ],
                    content =
                        [
                            Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = Schema(implementation = ProblemDetail::class),
                            ),
                        ],
                ),
                ApiResponse(
                    responseCode = "500",
                    description = "Required audit persistence or internal failure",
                    headers =
                        [
                            Header(
                                name = CorrelationIdFilter.HEADER_NAME,
                                schema = Schema(type = "string", format = "uuid"),
                            ),
                        ],
                    content =
                        [
                            Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = Schema(implementation = ProblemDetail::class),
                            ),
                        ],
                ),
            ],
    )
    @GetMapping("/{vin}/documents", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun search(
        @Parameter(
            description = "A 17-character VIN excluding I, O, and Q",
            example = "WVWZZZ1JZXW000001",
            schema = Schema(pattern = "^[A-HJ-NPR-Z0-9]{17}$"),
        )
        @PathVariable
        vin: String,
        request: HttpServletRequest,
    ): DocumentSearchResponse {
        val parsedVin =
            try {
                Vin.parse(vin)
            } catch (exception: IllegalArgumentException) {
                throw InvalidVinException(exception.message ?: "Invalid VIN")
            }
        return aggregationService.search(parsedVin, CorrelationIdFilter.from(request))
    }
}
