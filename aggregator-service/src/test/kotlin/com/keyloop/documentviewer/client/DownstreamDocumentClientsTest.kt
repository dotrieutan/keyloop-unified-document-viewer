package com.keyloop.documentviewer.client

import com.keyloop.documentviewer.domain.SourceStatus
import com.keyloop.documentviewer.domain.SourceSystem
import com.keyloop.documentviewer.domain.Vin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import java.util.UUID

class DownstreamDocumentClientsTest {
    private val vin = Vin.parse("WVWZZZ1JZXW000001")
    private val correlationId = UUID.fromString("28ac4434-cbef-46df-8876-28b73c52f864")

    @Test
    fun `sales client translates its source-specific schema`() {
        val builder = RestClient.builder().baseUrl("http://sales.test")
        val server = MockRestServiceServer.bindTo(builder).build()
        server
            .expect(requestTo("http://sales.test/api/sales/vehicles/${vin.value}/documents"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-Correlation-ID", correlationId.toString()))
            .andRespond(
                withSuccess(
                    """
                    {
                      "vehicleVin": "${vin.value}",
                      "records": [{
                        "documentId": "sale-1",
                        "category": "SALES_INVOICE",
                        "displayName": "Sales invoice",
                        "createdOn": "2026-07-01T08:00:00Z",
                        "downloadUri": "https://sales.example.test/sale-1"
                      }]
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val result = SalesDocumentClient(builder.build()).fetch(vin, correlationId)

        assertThat(result.status).isEqualTo(SourceStatus.SUCCESS)
        assertThat(result.documents.single().sourceSystem).isEqualTo(SourceSystem.SALES)
        assertThat(result.documents.single().title).isEqualTo("Sales invoice")
        server.verify()
    }

    @Test
    fun `service client translates its source-specific schema and query parameter`() {
        val builder = RestClient.builder().baseUrl("http://service.test")
        val server = MockRestServiceServer.bindTo(builder).build()
        server
            .expect(requestTo("http://service.test/api/service/documents?vin=${vin.value}"))
            .andRespond(
                withSuccess(
                    """
                    {
                      "vin": "${vin.value}",
                      "items": [{
                        "reference": "service-1",
                        "documentType": "INSPECTION_REPORT",
                        "description": "Inspection report",
                        "issuedAt": "2026-07-02T08:00:00Z",
                        "link": "https://service.example.test/service-1"
                      }]
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val result = ServiceDocumentClient(builder.build()).fetch(vin, correlationId)

        assertThat(result.status).isEqualTo(SourceStatus.SUCCESS)
        assertThat(result.documents.single().sourceSystem).isEqualTo(SourceSystem.SERVICE)
        assertThat(result.documents.single().type).isEqualTo("INSPECTION_REPORT")
        server.verify()
    }

    @Test
    fun `client rejects a response for another VIN without leaking its body`() {
        val builder = RestClient.builder().baseUrl("http://sales.test")
        val server = MockRestServiceServer.bindTo(builder).build()
        server
            .expect(requestTo("http://sales.test/api/sales/vehicles/${vin.value}/documents"))
            .andRespond(withSuccess("""{"vehicleVin":"WRONGVIN000000000","records":[]}""", MediaType.APPLICATION_JSON))

        val result = SalesDocumentClient(builder.build()).fetch(vin, correlationId)

        assertThat(result.status).isEqualTo(SourceStatus.INVALID_RESPONSE)
        assertThat(result.documents).isEmpty()
        server.verify()
    }
}
