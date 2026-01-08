# 📈 Kotlin Ktor Trading Backend SDK

A **professional-grade backend trading system** built with **Kotlin and Ktor**.  
This SDK exposes a **systematic REST API** for managing stock instruments, executing trades with persistent storage, and tracking real-time portfolio holdings.

---

## 🚀 Key Features

- 🔐 **Secure Authentication**  
  JWT (JSON Web Token) Bearer authentication for all protected trading endpoints.

- 💾 **Persistent Storage**  
  H2 relational database with JetBrains Exposed ORM ensures data survives server restarts.

- 📊 **Systematic Trading Engine**  
  Supports MARKET and LIMIT orders with strict price validation for LIMIT orders.

- 🧠 **Smart Portfolio Calculation**  
  Automatically calculates holdings using aggregated BUY − SELL transactions.

- 📘 **Interactive API Documentation**  
  Swagger UI integrated for live API testing.

---

## 🧩 Technology Stack

| Component        | Technology                     |
|------------------|--------------------------------|
| Language         | Kotlin                         |
| Server Framework | Ktor 3.x                       |
| Database         | H2 (Embedded / Persistent)     |
| ORM              | JetBrains Exposed              |
| Security         | JWT (HS256)                    |
| Serialization    | Kotlinx Serialization (JSON)   |

---

## 📡 API Documentation

### 🔓 Public Endpoints (No Token Required)

| Endpoint               | Method | Description |
|------------------------|--------|-------------|
| `/api/v1/login`        | POST   | Authenticate and receive JWT Bearer token |
| `/api/v1/instruments`  | GET    | Fetch available market instruments |

---

### 🔐 Protected Endpoints  
**Authorization Header Required:**  
`Authorization: Bearer <token>`

| Endpoint                   | Method | Description |
|----------------------------|--------|-------------|
| `/api/v1/orders`           | POST   | Place BUY/SELL order (MARKET/LIMIT) |
| `/api/v1/portfolio`        | GET    | View current holdings summary |
| `/api/v1/trades`           | GET    | Fetch complete trade history |
| `/api/v1/orders/{orderId}` | GET    | Get real-time order status |
| `/api/v1/profile`          | GET    | Fetch user profile & exchange permissions |

---

## 🧪 How to Run & Test

### 1️⃣ Start the Server

**Prerequisites**
- JDK 17+
- Gradle
- IntelliJ IDEA (recommended)

From the project root directory, run:

```bash
./gradlew run
