package com.keyloop.documentviewer

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPIDefinition(
    info =
        Info(
            title = "Keyloop Unified Document Viewer API",
            version = "1.0.0",
            description = "Aggregates vehicle document metadata from Sales and Service systems",
        ),
)
class DocumentViewerApplication

fun main(args: Array<String>) {
    runApplication<DocumentViewerApplication>(*args)
}
