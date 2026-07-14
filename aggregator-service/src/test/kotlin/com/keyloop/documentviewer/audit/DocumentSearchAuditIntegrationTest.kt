package com.keyloop.documentviewer.audit

import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.Instant
import java.util.UUID

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class DocumentSearchAuditIntegrationTest {
    @Autowired
    private lateinit var writer: SearchAuditWriter

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `Flyway schema accepts and reloads a privacy-safe audit record`() {
        val correlationId = UUID.randomUUID()
        val vin = Vin.parse("WVWZZZ1JZXW000001")
        val now = Instant.parse("2026-07-13T10:00:00Z")

        writer.write(
            SearchAuditRecord(
                correlationId = correlationId,
                vin = vin,
                requestedAt = now,
                completedAt = now.plusMillis(50),
                outcome = AuditOutcome.COMPLETE,
                sourceOutcomes =
                    mapOf(
                        SourceSystem.SALES to SourceStatus.SUCCESS,
                        SourceSystem.SERVICE to SourceStatus.EMPTY,
                    ),
                resultCount = 1,
            ),
        )

        val stored =
            jdbcTemplate.queryForMap(
                """
                SELECT vin_fingerprint, outcome, service_outcome
                FROM document_search_audit
                WHERE correlation_id = ?
                """.trimIndent(),
                correlationId,
            )
        assertThat(stored["vin_fingerprint"]).asString().hasSize(64).doesNotContain(vin.value)
        assertThat(stored["outcome"]).isEqualTo("COMPLETE")
        assertThat(stored["service_outcome"]).isEqualTo("EMPTY")
    }

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:18.4")

        @DynamicPropertySource
        @JvmStatic
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
