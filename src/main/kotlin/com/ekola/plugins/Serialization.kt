package com.ekola.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/version") {
                call.respond(mapOf("version" to "0.0.1"))
        }
    }
}
