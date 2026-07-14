package com.keyloop.mocks.sales

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MockSalesApplication

fun main(args: Array<String>) {
    runApplication<MockSalesApplication>(*args)
}
