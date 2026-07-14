package com.keyloop.documentviewer.audit

import com.keyloop.documentviewer.config.DocumentViewerProperties
import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import java.time.Duration
import java.time.Instant
import java.util.UUID

class AuditWriterTest {
    @Test
    fun `stores a deterministic HMAC fingerprint and no raw VIN`() {
        val aggregateTemplate = mock(JdbcAggregateTemplate::class.java)
        val writer = AuditWriter(aggregateTemplate, properties("test-secret"))
        val vin = Vin.parse("WVWZZZ1JZXW000001")
        val requestedAt = Instant.parse("2026-07-13T10:00:00Z")

        writer.write(
            SearchAuditRecord(
                correlationId = UUID.fromString("28ac4434-cbef-46df-8876-28b73c52f864"),
                vin = vin,
                requestedAt = requestedAt,
                completedAt = requestedAt.plusMillis(125),
                outcome = AuditOutcome.PARTIAL,
                sourceOutcomes =
                    mapOf(
                        SourceSystem.SALES to SourceStatus.SUCCESS,
                        SourceSystem.SERVICE to SourceStatus.TIMEOUT,
                    ),
                resultCount = 2,
            ),
        )

        val captor = ArgumentCaptor.forClass(DocumentSearchAudit::class.java)
        verify(aggregateTemplate).insert(captor.capture())
        val stored = captor.value
        assertThat(stored.vinFingerprint).hasSize(64).doesNotContain(vin.value)
        assertThat(stored.vinFingerprint).isEqualTo(writer.fingerprint(vin))
        assertThat(stored.durationMs).isEqualTo(125)
        assertThat(stored.salesOutcome).isEqualTo("SUCCESS")
        assertThat(stored.serviceOutcome).isEqualTo("TIMEOUT")
    }

    private fun properties(key: String) =
        DocumentViewerProperties(
            downstream =
                DocumentViewerProperties.Downstream(
                    sales = DocumentViewerProperties.Dependency("http://sales", Duration.ofSeconds(2)),
                    service = DocumentViewerProperties.Dependency("http://service", Duration.ofSeconds(2)),
                ),
            audit = DocumentViewerProperties.Audit(key),
        )
}
