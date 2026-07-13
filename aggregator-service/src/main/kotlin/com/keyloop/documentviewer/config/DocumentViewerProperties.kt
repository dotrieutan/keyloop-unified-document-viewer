package com.keyloop.documentviewer.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("document-viewer")
data class DocumentViewerProperties(
    val downstream: Downstream,
    val audit: Audit,
) {
    data class Downstream(
        val sales: Dependency,
        val service: Dependency,
    )

    data class Dependency(
        val baseUrl: String,
        val timeout: Duration,
    )

    data class Audit(
        val hmacKey: String,
    )
}
