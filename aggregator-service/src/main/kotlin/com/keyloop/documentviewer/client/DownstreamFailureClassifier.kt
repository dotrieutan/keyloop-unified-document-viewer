package com.keyloop.documentviewer.client

import com.keyloop.documentviewer.domain.SourceStatus
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import java.net.SocketTimeoutException
import java.net.http.HttpTimeoutException

internal fun classifyFailure(exception: Exception): SourceStatus =
    when (exception) {
        is RestClientResponseException -> SourceStatus.UNAVAILABLE
        is ResourceAccessException ->
            if (exception.causes().any { it is SocketTimeoutException || it is HttpTimeoutException }) {
                SourceStatus.TIMEOUT
            } else {
                SourceStatus.UNAVAILABLE
            }
        is RestClientException -> SourceStatus.INVALID_RESPONSE
        else -> SourceStatus.INVALID_RESPONSE
    }

private fun Throwable.causes(): Sequence<Throwable> = generateSequence(this) { current -> current.cause?.takeUnless { it === current } }
