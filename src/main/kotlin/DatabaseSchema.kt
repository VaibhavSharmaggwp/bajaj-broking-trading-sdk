package com.example

import org.jetbrains.exposed.sql.Table

object TradesTable: Table("traders"){
    val id = varchar("id", 50)
    val symbol = varchar("symbol", 20)
    val quantity = integer("quantity")
    val price = double("price")
    val type = varchar("type", 10)
    val timestamp = varchar("timestamp", 50)

    override val primaryKey = PrimaryKey(id)
}