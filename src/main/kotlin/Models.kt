package com.example

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String,
    val email: String,
    val exchanges: String,
    val user_name: String = "Sidharth" // We can give it a default name
)

@Serializable
data class ProfileResponse(
    val statusCode: Int,
    val message: String,
    val data: UserProfile
)

@Serializable
data class Instrument(
    val symbol: String,   // eg Reliance
    val exchange: String, // eg "NSE"
    val instrumentType: String, // Eg EQUITY
    val lastTradedPrice: Double, // this is current price
    val token: Int // // The unique ID from your data (e.g., 12)
)

@Serializable
data class OrderRequest(
    val symbol: String,
    val  quantity: Int,
    val price: Double?= null, // Optional: Only needed for LIMIT orders
    val type: String,   // BUY OR SELL
    val style: String // MARKET OR LIMIT

)

@Serializable
data class OrderResponse(
    val orderId: String,
    val status: String, // NEW, PLACED, EXECUTED, CANCELLED
    val message: String
)


@Serializable
data class Trade(
    val tradeId: String,
    val symbol: String,
    val quantity:Int,
    val type: String,
    val executionPrice: Double,
    val timestamp: String
)

@Serializable
data class PortfolioHolding(
    val symbol: String,
    val quantity: Int,
    val averagePrice: Double,
    val currentValue: Double,
)