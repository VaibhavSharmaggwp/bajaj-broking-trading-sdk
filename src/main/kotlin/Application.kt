package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    // 1. Connect to H2 Database (Creates a file named trading_db.mv.db)
    // We use the H2 driver because it is built into the JVM environment easily
    Database.connect(
        "jdbc:h2:./trading_db;MODE=MySQL;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )

    // 2. Initialize the table
    transaction {
        SchemaUtils.create(TradesTable)
    }

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Setup the guard (authentication)
    install(Authentication) {
        jwt("auth-jwt") {
            val jwtSecret = "my-super-secret-key"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer("bajaj-broking")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    // Global error handling
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid data format: ${cause.message}"))
        }

        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Something went wrong!"))
            cause.printStackTrace()
        }
    }

    // Initialize your other configurations
    configureSerialization()
    configureSecurity() // Note: If configureSecurity also has JWT setup, ensure they don't conflict
    configureRouting()
}