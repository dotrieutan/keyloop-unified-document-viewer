package com.keyloop.documentviewer.audit

import com.keyloop.documentviewer.config.DocumentViewerProperties
import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.HexFormat
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun interface SearchAuditWriter {
    fun write(record: SearchAuditRecord)
}

data class SearchAuditRecord(
    val correlationId: UUID,
    val vin: Vin,
    val requestedAt: Instant,
    val completedAt: Instant,
    val outcome: AuditOutcome,
    val sourceOutcomes: Map<SourceSystem, SourceStatus>,
    val resultCount: Int,
)

enum class AuditOutcome {
    COMPLETE,
    PARTIAL,
    FAILED,
}

@Component
class AuditWriter(
    private val aggregateTemplate: JdbcAggregateTemplate,
    properties: DocumentViewerProperties,
) : SearchAuditWriter {
    private val hmacKey = properties.audit.hmacKey.toByteArray(StandardCharsets.UTF_8)

    override fun write(record: SearchAuditRecord) {
        aggregateTemplate.insert(
            DocumentSearchAudit(
                id = UUID.randomUUID(),
                correlationId = record.correlationId,
                vinFingerprint = fingerprint(record.vin),
                requestedAt = record.requestedAt,
                completedAt = record.completedAt,
                outcome = record.outcome.name,
                salesOutcome = requireNotNull(record.sourceOutcomes[SourceSystem.SALES]).name,
                serviceOutcome = requireNotNull(record.sourceOutcomes[SourceSystem.SERVICE]).name,
                resultCount = record.resultCount,
                durationMs = Duration.between(record.requestedAt, record.completedAt).toMillis().coerceAtLeast(0),
            ),
        )
    }

    internal fun fingerprint(vin: Vin): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(hmacKey, "HmacSHA256"))
        return HexFormat.of().formatHex(mac.doFinal(vin.value.toByteArray(StandardCharsets.UTF_8)))
    }
}
