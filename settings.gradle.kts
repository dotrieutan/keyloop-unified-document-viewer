plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "keyloop-unified-document-viewer"

include(
    "aggregator-service",
    "mock-sales-service",
    "mock-service-service",
)

