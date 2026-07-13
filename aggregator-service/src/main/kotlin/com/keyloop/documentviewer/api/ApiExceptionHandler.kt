package com.keyloop.documentviewer.api

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class ApiExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(InvalidVinException::class)
    fun invalidVin(
        exception: InvalidVinException,
        request: HttpServletRequest,
    ): ProblemDetail =
        problem(
            status = HttpStatus.BAD_REQUEST,
            type = "https://keyloop.example/problems/invalid-vin",
            title = "Invalid VIN",
            detail = exception.message ?: "The VIN is invalid",
            request = request,
        )

    @ExceptionHandler(AllSourcesUnavailableException::class)
    fun allSourcesUnavailable(
        exception: AllSourcesUnavailableException,
        request: HttpServletRequest,
    ): ProblemDetail =
        problem(
            status = HttpStatus.SERVICE_UNAVAILABLE,
            type = "https://keyloop.example/problems/document-sources-unavailable",
            title = "Document sources unavailable",
            detail = exception.message ?: "No document source is currently available",
            request = request,
        ).also {
            it.setProperty(
                "sources",
                exception.sources.map { source ->
                    mapOf("sourceSystem" to source.sourceSystem, "status" to source.status)
                },
            )
        }

    @ExceptionHandler(Exception::class)
    fun unexpected(
        exception: Exception,
        request: HttpServletRequest,
    ): ProblemDetail {
        logger.error("event=document_search_unexpected_error", exception)
        return problem(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            type = "https://keyloop.example/problems/internal-error",
            title = "Internal server error",
            detail = "The document search could not be completed",
            request = request,
        )
    }

    private fun problem(
        status: HttpStatus,
        type: String,
        title: String,
        detail: String,
        request: HttpServletRequest,
    ): ProblemDetail =
        ProblemDetail.forStatusAndDetail(status, detail).apply {
            this.type = URI.create(type)
            this.title = title
            setProperty("correlationId", CorrelationIdFilter.from(request))
        }
}
