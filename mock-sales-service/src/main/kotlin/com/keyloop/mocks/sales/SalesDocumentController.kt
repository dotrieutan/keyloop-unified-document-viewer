package com.keyloop.mocks.sales

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.time.Instant
import java.util.UUID

@RestController
class SalesDocumentController {
    @GetMapping("/api/sales/vehicles/{vin}/documents")
    fun documents(
        @PathVariable vin: String,
        @RequestHeader("X-Correlation-ID") correlationId: UUID,
    ): SalesDocumentResponse {
        if (vin.endsWith("2") || vin.endsWith("5")) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mock sales system failure")
        }
        if (vin.endsWith("4")) {
            return SalesDocumentResponse(vin, emptyList())
        }

        return SalesDocumentResponse(
            vehicleVin = vin,
            records =
                listOf(
                    SalesDocument(
                        documentId = "sale-$vin-1",
                        category = "SALES_INVOICE",
                        displayName = "Vehicle sales invoice",
                        createdOn = Instant.parse("2026-06-10T09:30:00Z"),
                        downloadUri = URI.create("https://sales.example.test/documents/sale-$vin-1"),
                    ),
                    SalesDocument(
                        documentId = "sale-$vin-2",
                        category = "PURCHASE_AGREEMENT",
                        displayName = "Signed purchase agreement",
                        createdOn = Instant.parse("2026-06-09T15:00:00Z"),
                        downloadUri = URI.create("https://sales.example.test/documents/sale-$vin-2"),
                    ),
                ),
        )
    }
}

data class SalesDocumentResponse(
    val vehicleVin: String,
    val records: List<SalesDocument>,
)

data class SalesDocument(
    val documentId: String,
    val category: String,
    val displayName: String,
    val createdOn: Instant,
    val downloadUri: URI,
)
