package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/**
 * Auth Routes: Handles user login and JWT generation.
 */
fun Route.authRoutes() {
    post("/api/v1/login") {
        val jwtSecret = "my-super-secret-key"

        // Mock login: Generates a token for the user ID
        val token = JWT.create()
            .withIssuer("bajaj-broking")
            .withClaim("userId", "ONL12372")
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000)) // 1 hour expiry
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(mapOf("token" to token))
    }
}

/**
 * Main Routing Configuration.
 */
fun Application.configureRouting() {
    routing {

        // 1. Swagger UI - Interactive API documentation
        // Access at: http://127.0.0.1:8080/swagger
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }

        // 2. Public Routes
        authRoutes()

        get("/test") {
            call.respondText("I am a KTOR developer now")
        }

        // Requirement: Fetch list of tradable instruments
        get("/api/v1/instruments") {
            call.respond(TradingRepository.instruments)
        }

        // 3. Protected Routes - Requires Bearer Token
        authenticate("auth-jwt") {

            // Requirement: Fetch profile details
            get("/api/v1/profile") {
                val response = ProfileResponse(
                    statusCode = 0,
                    message = "Success",
                    data = TradingRepository.mockUser
                )
                call.respond(response)
            }

            // Requirement: Place a New Order
            post("/api/v1/orders") {
                val request = call.receive<OrderRequest>()

                // Validation: Quantity check
                if (request.quantity <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Quantity must be greater than zero")
                    return@post
                }

                // Validation: LIMIT order price check
                if (request.style.uppercase() == "LIMIT" && (request.price == null || request.price <= 0)) {
                    call.respond(HttpStatusCode.BadRequest, "Price is mandatory for LIMIT orders")
                    return@post
                }

                val response = TradingRepository.processOrder(request)
                call.respond(response)
            }

            // Requirement: Fetch Order Status (Dynamic parameter)
            get("/api/v1/orders/{orderId}") {
                val id = call.parameters["orderId"] ?: "N/A"
                call.respond(mapOf(
                    "orderId" to id,
                    "status" to "EXECUTED",
                    "message" to "Order details for $id retrieved successfully"
                ))
            }

            // Requirement: Fetch list of executed trades
            get("/api/v1/trades") {
                val allTrades = TradingRepository.getAllTradesFromDb()
                call.respond(allTrades)
            }

            // Requirement: Fetch current portfolio holdings
            get("/api/v1/portfolio") {
                val summary = TradingRepository.getPortfolio()
                call.respond(summary)
            }
        }
    }
}