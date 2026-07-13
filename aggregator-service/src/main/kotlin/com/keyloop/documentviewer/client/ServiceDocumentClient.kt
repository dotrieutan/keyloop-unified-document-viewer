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
import java.net.URI
import java.time.Instant
import java.util.UUID

@Component
class ServiceDocumentClient(
    @Qualifier("serviceRestClient") private val restClient: RestClient,
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
                        .uri { builder -> builder.path("/api/service/documents").queryParam("vin", vin.value).build() }
                        .header("X-Correlation-ID", correlationId.toString())
                        .retrieve()
                        .body(ServiceDocumentResponse::class.java),
                ) { "Service response body is missing" }
            require(response.vin == vin.value) { "Service response VIN does not match the request" }
            val documents = response.items.map { it.toDocument() }
            SourceFetchResult(
                sourceSystem = SourceSystem.SERVICE,
                status = if (documents.isEmpty()) SourceStatus.EMPTY else SourceStatus.SUCCESS,
                documents = documents,
            )
        } catch (exception: Exception) {
            val status = classifyFailure(exception)
            logger.warn(
                "event=downstream_call_failed correlationId={} source=SERVICE status={}",
                correlationId,
                status,
            )
            SourceFetchResult(SourceSystem.SERVICE, status)
        }

    private fun ServiceDocument.toDocument() =
        DocumentMetadata(
            id = reference,
            sourceSystem = SourceSystem.SERVICE,
            type = documentType,
            title = description,
            createdAt = issuedAt,
            downloadUrl = link,
        )
}

private data class ServiceDocumentResponse(
    val vin: String,
    val items: List<ServiceDocument>,
)

private data class ServiceDocument(
    val reference: String,
    val documentType: String,
    val description: String,
    val issuedAt: Instant,
    val link: URI,
)
