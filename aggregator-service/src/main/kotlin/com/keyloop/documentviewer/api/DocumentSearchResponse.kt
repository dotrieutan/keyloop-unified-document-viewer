package com.keyloop.documentviewer.api

import com.keyloop.documentviewer.domain.DocumentMetadata
import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import java.time.Instant
import java.util.UUID

enum class SearchStatus {
    COMPLETE,
    PARTIAL,
}

data class DocumentSearchResponse(
    val correlationId: UUID,
    val vin: String,
    val status: SearchStatus,
    val retrievedAt: Instant,
    val documents: List<DocumentMetadata>,
    val sources: List<SourceSummary>,
    val warnings: List<SourceWarning>,
)

data class SourceSummary(
    val sourceSystem: SourceSystem,
    val status: SourceStatus,
    val documentCount: Int,
)

data class SourceWarning(
    val sourceSystem: SourceSystem,
    val code: WarningCode,
    val message: String,
)

enum class WarningCode {
    TIMEOUT,
    UNAVAILABLE,
    INVALID_RESPONSE,
}
