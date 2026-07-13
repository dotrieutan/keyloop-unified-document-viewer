package com.keyloop.documentviewer.service

import com.keyloop.documentviewer.api.AllSourcesUnavailableException
import com.keyloop.documentviewer.api.SearchStatus
import com.keyloop.documentviewer.api.WarningCode
import com.keyloop.documentviewer.audit.SearchAuditRecord
import com.keyloop.documentviewer.audit.SearchAuditWriter
import com.keyloop.documentviewer.domain.DocumentMetadata
import com.keyloop.documentviewer.domain.DocumentSourceClient
import com.keyloop.documentviewer.domain.SourceFetchResult
import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit

class DocumentAggregationServiceTest {
    private val vin = Vin.parse("WVWZZZ1JZXW000001")
    private val correlationId = UUID.fromString("28ac4434-cbef-46df-8876-28b73c52f864")
    private val clock = Clock.fixed(Instant.parse("2026-07-13T10:00:00Z"), ZoneOffset.UTC)

    @Test
    fun `returns a complete deterministic response and deduplicates within a source`(): Unit =
        runBlocking {
            val newer = document("same-id", SourceSystem.SALES, "Zulu", "2026-07-02T00:00:00Z")
            val tieWinner = document("same-id", SourceSystem.SALES, "Alpha", "2026-07-02T00:00:00Z")
            val older = document("same-id", SourceSystem.SALES, "Older", "2026-07-01T00:00:00Z")
            val sameIdOtherSource = document("same-id", SourceSystem.SERVICE, "Service", "2026-07-03T00:00:00Z")
            val audit = RecordingAuditWriter()
            val service =
                service(
                    clients =
                        listOf(
                            client(SourceSystem.SALES, listOf(older, newer, tieWinner)),
                            client(SourceSystem.SERVICE, listOf(sameIdOtherSource)),
                        ),
                    audit = audit,
                )

            val response = service.search(vin, correlationId)

            assertThat(response.status).isEqualTo(SearchStatus.COMPLETE)
            assertThat(response.documents).extracting<String> { it.title }.containsExactly("Service", "Alpha")
            assertThat(response.sources).allMatch { it.status == SourceStatus.SUCCESS }
            assertThat(response.warnings).isEmpty()
            assertThat(audit.records.single().outcome).isEqualTo("COMPLETE")
            assertThat(audit.records.single().resultCount).isEqualTo(2)
        }

    @Test
    fun `returns successful documents and a safe warning when one source times out`(): Unit =
        runBlocking {
            val audit = RecordingAuditWriter()
            val service =
                service(
                    listOf(
                        client(SourceSystem.SALES, listOf(document("sales-1", SourceSystem.SALES))),
                        failedClient(SourceSystem.SERVICE, SourceStatus.TIMEOUT),
                    ),
                    audit,
                )

            val response = service.search(vin, correlationId)

            assertThat(response.status).isEqualTo(SearchStatus.PARTIAL)
            assertThat(response.documents).hasSize(1)
            assertThat(response.warnings.single().code).isEqualTo(WarningCode.TIMEOUT)
            assertThat(response.warnings.single().message).doesNotContain("exception", "localhost")
            assertThat(audit.records.single().outcome).isEqualTo("PARTIAL")
        }

    @Test
    fun `persists a failed audit then raises service unavailable when both sources fail`() {
        val audit = RecordingAuditWriter()
        val service =
            service(
                listOf(
                    failedClient(SourceSystem.SALES, SourceStatus.UNAVAILABLE),
                    failedClient(SourceSystem.SERVICE, SourceStatus.TIMEOUT),
                ),
                audit,
            )

        val exception = assertThrows<AllSourcesUnavailableException> { runBlocking { service.search(vin, correlationId) } }

        assertThat(exception.sources)
            .extracting<SourceStatus> { it.status }
            .containsExactly(SourceStatus.UNAVAILABLE, SourceStatus.TIMEOUT)
        assertThat(audit.records.single().outcome).isEqualTo("FAILED")
        assertThat(audit.records.single().resultCount).isZero()
    }

    @Test
    fun `starts both blocking source calls concurrently`(): Unit =
        runBlocking {
            val barrier = CyclicBarrier(2)
            val clients =
                SourceSystem.entries.map { source ->
                    DocumentSourceClient { _, _ ->
                        barrier.await(1, TimeUnit.SECONDS)
                        SourceFetchResult(source, SourceStatus.EMPTY)
                    }
                }

            val response = service(clients, RecordingAuditWriter()).search(vin, correlationId)

            assertThat(response.status).isEqualTo(SearchStatus.COMPLETE)
            assertThat(response.documents).isEmpty()
        }

    private fun service(
        clients: List<DocumentSourceClient>,
        audit: SearchAuditWriter,
    ) = DocumentAggregationService(clients, audit, clock, SimpleMeterRegistry())

    private fun client(
        source: SourceSystem,
        documents: List<DocumentMetadata>,
    ) = DocumentSourceClient { _, _ -> SourceFetchResult(source, SourceStatus.SUCCESS, documents) }

    private fun failedClient(
        source: SourceSystem,
        status: SourceStatus,
    ) = DocumentSourceClient { _, _ -> SourceFetchResult(source, status) }

    private fun document(
        id: String,
        source: SourceSystem,
        title: String = id,
        createdAt: String = "2026-07-01T00:00:00Z",
    ) = DocumentMetadata(
        id = id,
        sourceSystem = source,
        type = "TEST",
        title = title,
        createdAt = Instant.parse(createdAt),
        downloadUrl = URI.create("https://example.test/$id"),
    )

    private class RecordingAuditWriter : SearchAuditWriter {
        val records = mutableListOf<SearchAuditRecord>()

        override fun write(record: SearchAuditRecord) {
            records += record
        }
    }
}
