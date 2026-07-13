package com.keyloop.documentviewer.domain

import java.net.URI
import java.time.Instant

enum class SourceSystem {
    SALES,
    SERVICE,
}

enum class SourceStatus {
    SUCCESS,
    EMPTY,
    TIMEOUT,
    UNAVAILABLE,
    INVALID_RESPONSE,
}

data class DocumentMetadata(
    val id: String,
    val sourceSystem: SourceSystem,
    val type: String,
    val title: String,
    val createdAt: Instant,
    val downloadUrl: URI,
)

data class SourceFetchResult(
    val sourceSystem: SourceSystem,
    val status: SourceStatus,
    val documents: List<DocumentMetadata> = emptyList(),
) {
    val isUsable: Boolean = status == SourceStatus.SUCCESS || status == SourceStatus.EMPTY

    init {
        require(isUsable || documents.isEmpty()) { "Failed source results cannot contain documents" }
    }
}
