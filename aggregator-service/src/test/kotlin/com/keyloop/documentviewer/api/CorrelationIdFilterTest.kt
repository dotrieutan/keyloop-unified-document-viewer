package com.keyloop.documentviewer.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID

class CorrelationIdFilterTest {
    private val filter = CorrelationIdFilter()

    @Test
    fun `propagates a valid request correlation ID`() {
        val id = UUID.fromString("28ac4434-cbef-46df-8876-28b73c52f864")
        val request = MockHttpServletRequest().apply { addHeader(CorrelationIdFilter.HEADER_NAME, id.toString()) }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, MockFilterChain())

        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo(id.toString())
        assertThat(CorrelationIdFilter.from(request)).isEqualTo(id)
    }

    @Test
    fun `replaces an invalid request correlation ID`() {
        val request = MockHttpServletRequest().apply { addHeader(CorrelationIdFilter.HEADER_NAME, "not-a-uuid") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, MockFilterChain())

        assertThat(UUID.fromString(response.getHeader(CorrelationIdFilter.HEADER_NAME))).isNotNull()
    }
}
