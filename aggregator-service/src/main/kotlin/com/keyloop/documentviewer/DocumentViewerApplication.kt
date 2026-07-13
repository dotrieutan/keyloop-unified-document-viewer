package com.keyloop.documentviewer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DocumentViewerApplication

fun main(args: Array<String>) {
    runApplication<DocumentViewerApplication>(*args)
}
