package com.keyloop.documentviewer.api

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class CorrelationIdFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val correlationId =
            request
                .getHeader(HEADER_NAME)
                ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: UUID.randomUUID()

        request.setAttribute(REQUEST_ATTRIBUTE, correlationId)
        response.setHeader(HEADER_NAME, correlationId.toString())
        MDC.put(MDC_KEY, correlationId.toString())
        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(MDC_KEY)
        }
    }

    companion object {
        const val HEADER_NAME = "X-Correlation-ID"
        const val REQUEST_ATTRIBUTE = "documentViewerCorrelationId"
        private const val MDC_KEY = "correlationId"

        fun from(request: HttpServletRequest): UUID =
            requireNotNull(request.getAttribute(REQUEST_ATTRIBUTE) as? UUID) {
                "Correlation ID filter did not run"
            }
    }
}
