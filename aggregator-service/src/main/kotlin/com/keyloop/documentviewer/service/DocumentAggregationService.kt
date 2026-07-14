package com.keyloop.documentviewer.service

import com.keyloop.documentviewer.api.AllSourcesUnavailableException
import com.keyloop.documentviewer.api.DocumentSearchResponse
import com.keyloop.documentviewer.api.SearchStatus
import com.keyloop.documentviewer.api.SourceSummary
import com.keyloop.documentviewer.api.SourceWarning
import com.keyloop.documentviewer.api.WarningCode
import com.keyloop.documentviewer.audit.AuditOutcome
import com.keyloop.documentviewer.audit.SearchAuditRecord
import com.keyloop.documentviewer.audit.SearchAuditWriter
import com.keyloop.documentviewer.domain.DocumentMetadata
import com.keyloop.documentviewer.domain.DocumentSourceClient
import com.keyloop.documentviewer.domain.SourceFetchResult
import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class DocumentAggregationService(
    private val clients: List<DocumentSourceClient>,
    private val auditWriter: SearchAuditWriter,
    private val clock: Clock,
    private val meterRegistry: MeterRegistry,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        require(clients.size == SourceSystem.entries.size) { "Exactly one client per document source is required" }
    }

    suspend fun search(
        vin: Vin,
        correlationId: UUID,
    ): DocumentSearchResponse {
        val requestedAt = clock.instant()
        val sourceResults = fetchAll(vin, correlationId).sortedBy { it.sourceSystem }
        val usableResults = sourceResults.filter { it.isUsable }
        val documents = normalize(usableResults.flatMap { it.documents })
        val completedAt = clock.instant()
        val duration = Duration.between(requestedAt, completedAt).takeUnless { it.isNegative } ?: Duration.ZERO
        val outcome =
            when (usableResults.size) {
                SourceSystem.entries.size -> AuditOutcome.COMPLETE
                0 -> AuditOutcome.FAILED
                else -> AuditOutcome.PARTIAL
            }

        auditWriter.write(
            SearchAuditRecord(
                correlationId = correlationId,
                vin = vin,
                requestedAt = requestedAt,
                completedAt = completedAt,
                outcome = outcome,
                sourceOutcomes = sourceResults.associate { it.sourceSystem to it.status },
                resultCount = documents.size,
            ),
        )
        meterRegistry.counter("document.search.requests", "outcome", outcome.name).increment()
        Timer
            .builder("document.search.duration")
            .tag("outcome", outcome.name)
            .register(meterRegistry)
            .record(duration)
        meterRegistry.summary("document.search.result.count", "outcome", outcome.name).record(documents.size.toDouble())
        sourceResults.forEach { source ->
            meterRegistry
                .counter(
                    "document.downstream.requests",
                    "source",
                    source.sourceSystem.name,
                    "outcome",
                    source.status.name,
                ).increment()
        }
        logger.info(
            "event=document_search_completed correlationId={} outcome={} durationMs={} resultCount={} " +
                "salesOutcome={} serviceOutcome={}",
            correlationId,
            outcome,
            duration.toMillis(),
            documents.size,
            sourceResults.first { it.sourceSystem == SourceSystem.SALES }.status,
            sourceResults.first { it.sourceSystem == SourceSystem.SERVICE }.status,
        )

        if (usableResults.isEmpty()) {
            throw AllSourcesUnavailableException(sourceResults)
        }

        return DocumentSearchResponse(
            correlationId = correlationId,
            vin = vin.value,
            status = if (outcome == AuditOutcome.COMPLETE) SearchStatus.COMPLETE else SearchStatus.PARTIAL,
            retrievedAt = completedAt,
            documents = documents,
            sources =
                sourceResults.map {
                    SourceSummary(it.sourceSystem, it.status, it.documents.size)
                },
            warnings = sourceResults.filterNot { it.isUsable }.map(::warningFor),
        )
    }

    private suspend fun fetchAll(
        vin: Vin,
        correlationId: UUID,
    ): List<SourceFetchResult> =
        coroutineScope {
            clients
                .map { client ->
                    async(Dispatchers.IO) {
                        val startedAt = System.nanoTime()
                        val result = client.fetch(vin, correlationId)
                        Timer
                            .builder("document.downstream.duration")
                            .tag("source", result.sourceSystem.name)
                            .tag("outcome", result.status.name)
                            .register(meterRegistry)
                            .record(System.nanoTime() - startedAt, TimeUnit.NANOSECONDS)
                        result
                    }
                }.awaitAll()
        }

    internal fun normalize(documents: List<DocumentMetadata>): List<DocumentMetadata> =
        documents
            .groupBy { it.sourceSystem to it.id }
            .values
            .map { duplicates ->
                duplicates.minWith(compareByDescending<DocumentMetadata> { it.createdAt }.thenBy { it.title })
            }.sortedWith(
                compareByDescending<DocumentMetadata> { it.createdAt }
                    .thenBy { it.sourceSystem }
                    .thenBy { it.id },
            )

    private fun warningFor(result: SourceFetchResult): SourceWarning {
        val (code, message) =
            when (result.status) {
                SourceStatus.TIMEOUT -> WarningCode.TIMEOUT to "The source did not respond before its deadline"
                SourceStatus.UNAVAILABLE -> WarningCode.UNAVAILABLE to "The source is temporarily unavailable"
                SourceStatus.INVALID_RESPONSE -> WarningCode.INVALID_RESPONSE to "The source returned an unusable response"
                else -> error("Usable source results do not produce warnings")
            }
        return SourceWarning(
            sourceSystem = result.sourceSystem,
            code = code,
            message = message,
        )
    }
}
