package com.keyloop.mocks.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MockServiceApplication

fun main(args: Array<String>) {
    runApplication<MockServiceApplication>(*args)
}
