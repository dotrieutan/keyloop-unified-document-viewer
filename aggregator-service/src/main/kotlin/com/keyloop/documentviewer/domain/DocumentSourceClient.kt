package com.keyloop.documentviewer.domain

import java.util.UUID

fun interface DocumentSourceClient {
    fun fetch(
        vin: Vin,
        correlationId: UUID,
    ): SourceFetchResult
}
