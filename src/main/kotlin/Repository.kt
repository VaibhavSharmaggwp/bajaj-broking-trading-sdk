package com.example

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object TradingRepository {
    // 1. Mock User Data
    val mockUser = UserProfile(
        userId = "ONL12372",
        email = "SIDHARTH@EXAMPLE.COM",
        exchanges = "BSE,NSE,NFO",
        user_name = "Sidharth"
    )

    // 2. Static list of available stocks
    val instruments = listOf(
        Instrument("RELIANCE", "NSE", "EQUITY", 2500.0, 101),
        Instrument("TCS", "NSE", "EQUITY", 3400.0, 102),
        Instrument("BSE-INDEX", "BSE", "INDEX", 52470.88, 12)
    )

    /**
     * Handles incoming orders.
     * Logic: MARKET orders are executed and saved to the H2 database immediately.
     */
    fun processOrder(request: OrderRequest): OrderResponse {
        val newId = "ORD-${UUID.randomUUID().toString().take(5)}"

        if (request.style.uppercase() == "MARKET") {
            val executionPrice = 2500.0

            val newTrade = Trade(
                tradeId = "TRD-${UUID.randomUUID().toString().take(5)}",
                symbol = request.symbol,
                quantity = request.quantity,
                executionPrice = executionPrice,
                type = request.type.uppercase(), // "BUY" or "SELL"
                timestamp = java.time.LocalDateTime.now().toString()
            )

            // Save to H2 Database
            saveTradeToDb(newTrade)

            return OrderResponse(newId, "EXECUTED", "Order executed successfully at $executionPrice")
        }

        return OrderResponse(newId, "PLACED", "Limit order placed at ${request.price}")
    }

    /**
     * Logic to group trades from the Database into a Portfolio summary.
     * Fix: Subtracts quantity if the trade type is "SELL".
     */
    fun getPortfolio(): List<PortfolioHolding> {
        val allTrades = getAllTradesFromDb()
        val groupedBySymbol = allTrades.groupBy { it.symbol }

        return groupedBySymbol.map { (symbol, tradesList) ->
            // SELL orders subtract from the total quantity
            val totalQty = tradesList.sumOf {
                if (it.type == "SELL") -it.quantity else it.quantity
            }

            // Average price based on BUY orders only
            val buyTrades = tradesList.filter { it.type == "BUY" }
            val avgPrice = if (buyTrades.isNotEmpty()) buyTrades.map { it.executionPrice }.average() else 0.0

            PortfolioHolding(
                symbol = symbol,
                quantity = totalQty,
                averagePrice = avgPrice,
                currentValue = totalQty * 2500.0 // Mock current market price
            )
        }.filter { it.quantity > 0 } // Only show stocks you still hold
    }

    /**
     * Database Helper: Saves a trade to H2
     */
    fun saveTradeToDb(trade: Trade) {
        transaction {
            TradesTable.insert {
                it[id] = trade.tradeId
                it[symbol] = trade.symbol
                it[quantity] = trade.quantity
                it[price] = trade.executionPrice
                it[type] = trade.type
                it[timestamp] = trade.timestamp
            }
        }
    }

    /**
     * Database Helper: Reads all trades from H2
     */
    fun getAllTradesFromDb(): List<Trade> {
        return transaction {
            TradesTable.selectAll().map {
                Trade(
                    tradeId = it[TradesTable.id],
                    symbol = it[TradesTable.symbol],
                    quantity = it[TradesTable.quantity],
                    executionPrice = it[TradesTable.price],
                    type = it[TradesTable.type],
                    timestamp = it[TradesTable.timestamp]
                )
            }
        }
    }
}