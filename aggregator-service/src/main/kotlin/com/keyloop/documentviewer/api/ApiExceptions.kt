package com.keyloop.documentviewer.api

import com.keyloop.documentviewer.domain.SourceFetchResult

class InvalidVinException(
    message: String,
) : RuntimeException(message)

class AllSourcesUnavailableException(
    val sources: List<SourceFetchResult>,
) : RuntimeException("No document source produced a usable response")
