package com.keyloop.mocks.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.time.Instant
import java.util.UUID

@RestController
class ServiceDocumentController {
    @GetMapping("/api/service/documents")
    fun documents(
        @RequestParam vin: String,
        @RequestHeader("X-Correlation-ID") correlationId: UUID,
    ): ServiceDocumentResponse {
        if (vin.endsWith("5")) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mock service system failure")
        }
        if (vin.endsWith("3")) {
            Thread.sleep(3_000)
        }
        if (vin.endsWith("4")) {
            return ServiceDocumentResponse(vin, emptyList())
        }

        return ServiceDocumentResponse(
            vin = vin,
            items =
                listOf(
                    ServiceDocument(
                        reference = "service-$vin-1",
                        documentType = "SERVICE_INVOICE",
                        description = "Annual vehicle service invoice",
                        issuedAt = Instant.parse("2026-07-01T08:15:00Z"),
                        link = URI.create("https://service.example.test/documents/service-$vin-1"),
                    ),
                    ServiceDocument(
                        reference = "service-$vin-2",
                        documentType = "INSPECTION_REPORT",
                        description = "Vehicle inspection report",
                        issuedAt = Instant.parse("2026-07-01T08:10:00Z"),
                        link = URI.create("https://service.example.test/documents/service-$vin-2"),
                    ),
                ),
        )
    }
}

data class ServiceDocumentResponse(
    val vin: String,
    val items: List<ServiceDocument>,
)

data class ServiceDocument(
    val reference: String,
    val documentType: String,
    val description: String,
    val issuedAt: Instant,
    val link: URI,
)
