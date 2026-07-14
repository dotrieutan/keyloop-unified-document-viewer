package com.keyloop.documentviewer.client

import com.keyloop.documentviewer.domain.DocumentMetadata
import com.keyloop.documentviewer.domain.DocumentSourceClient
import com.keyloop.documentviewer.domain.SourceFetchResult
import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.net.URI
import java.time.Instant
import java.util.UUID

@Component
class SalesDocumentClient(
    @Qualifier("salesRestClient") private val restClient: RestClient,
) : DocumentSourceClient {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun fetch(
        vin: Vin,
        correlationId: UUID,
    ): SourceFetchResult =
        try {
            val response =
                requireNotNull(
                    restClient
                        .get()
                        .uri("/api/sales/vehicles/{vin}/documents", vin.value)
                        .header("X-Correlation-ID", correlationId.toString())
                        .retrieve()
                        .body<SalesDocumentResponse>(),
                ) { "Sales response body is missing" }
            require(response.vehicleVin == vin.value) { "Sales response VIN does not match the request" }
            val documents = response.records.map { it.toDocument() }
            SourceFetchResult(
                sourceSystem = SourceSystem.SALES,
                status = if (documents.isEmpty()) SourceStatus.EMPTY else SourceStatus.SUCCESS,
                documents = documents,
            )
        } catch (exception: Exception) {
            val status = classifyFailure(exception)
            logger.warn(
                "event=downstream_call_failed correlationId={} source=SALES status={}",
                correlationId,
                status,
            )
            SourceFetchResult(SourceSystem.SALES, status)
        }

    private fun SalesDocument.toDocument() =
        DocumentMetadata(
            id = documentId,
            sourceSystem = SourceSystem.SALES,
            type = category,
            title = displayName,
            createdAt = createdOn,
            downloadUrl = downloadUri,
        )
}

private data class SalesDocumentResponse(
    val vehicleVin: String,
    val records: List<SalesDocument>,
)

private data class SalesDocument(
    val documentId: String,
    val category: String,
    val displayName: String,
    val createdOn: Instant,
    val downloadUri: URI,
)
