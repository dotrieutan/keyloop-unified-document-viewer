package com.keyloop.documentviewer.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Clock

@Configuration
class ApplicationConfiguration {
    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean("salesRestClient")
    fun salesRestClient(properties: DocumentViewerProperties): RestClient = restClient(properties.downstream.sales)

    @Bean("serviceRestClient")
    fun serviceRestClient(properties: DocumentViewerProperties): RestClient = restClient(properties.downstream.service)

    private fun restClient(dependency: DocumentViewerProperties.Dependency): RestClient {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(dependency.timeout)
        requestFactory.setReadTimeout(dependency.timeout)
        return RestClient
            .builder()
            .baseUrl(dependency.baseUrl)
            .requestFactory(requestFactory)
            .build()
    }
}
