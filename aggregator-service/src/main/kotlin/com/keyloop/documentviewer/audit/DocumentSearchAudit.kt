package com.keyloop.documentviewer.audit

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("document_search_audit")
data class DocumentSearchAudit(
    @Id val id: UUID,
    val correlationId: UUID,
    val vinFingerprint: String,
    val requestedAt: Instant,
    val completedAt: Instant,
    val outcome: String,
    val salesOutcome: String,
    val serviceOutcome: String,
    val resultCount: Int,
    val durationMs: Long,
)
