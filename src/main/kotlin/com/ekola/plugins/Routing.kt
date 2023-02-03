package com.ekola.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*


fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    
    routing {
        get("/") {
            val sampleUser = User("John", 30)
            call.respond(FreeMarkerContent("index.ftl", mapOf("user" to sampleUser)))

        }


        get("/info") {
            call.respondText("Nisi app!")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
