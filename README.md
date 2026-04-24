# 🏪 Store Management System

A comprehensive **Java** application for managing a retail store.  
Developed as a university project to demonstrate **object-oriented design**, **layered architecture**, **dependency injection**, **exception handling**, **file I/O**, and **unit/integration testing**.

---

## 📋 Table of Contents
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Architecture & Design](#-architecture--design)
- [SOLID Principles & Best Practices](#-solid-principles--best-practices)
- [Testing Strategy](#-testing-strategy)
- [Known Issues & TODOs](#-known-issues--todos)
- [Getting Started](#-getting-started)
- [Author & Contact](#-author--contact)

---

## ✨ Features

- **Product Management** – add food/non-food products with expiry dates, track deliveries & sales.
- **Inventory Control** – real‑time stock tracking with custom `InsufficientStockException`.
- **Dynamic Pricing** – configurable markup per category, automatic discount when approaching expiration.
- **Cashier & Register Management** – assign cashiers to registers, prevent conflicts.
- **Customer Wallet** – check affordability, deduct payments.
- **Receipt Generation** – sequential receipt IDs, timestamp, itemised list, total.
- **Persistence** – save each receipt as a human‑readable `.txt` file, using unique file names.
- **Financial Reporting** – total revenue, total supply costs, receipt count.
- **Exception Handling** – custom exceptions, clear error messages, fail‑fast validation.

---

## 🛠 Technology Stack

| Layer           | Technology                                  |
|-----------------|---------------------------------------------|
| Language        | Java 17+                                    |
| Testing         | JUnit 5 (Jupiter)                           |
| Build           | Plain Java (compile with `javac`)           |
| File I/O        | `java.io.FileWriter`, `java.nio.file.Files` |
| Date/Time       | `java.time.LocalDate`, `LocalDateTime`      |
| Version Control | Git, GitHub                                 |

---

## 📁 Project Structure

src/

├── main/

│ └── store/

│ ├── model/ ← Domain entities (POJOs)

│ │ ├── Product.java

│ │ ├── ProductCategory.java (enum)

│ │ ├── Cashier.java

│ │ ├── Register.java

│ │ ├── Customer.java

│ │ ├── Receipt.java

│ │ └── ReceiptItem.java

│ ├── service/ ← Business logic interfaces & implementations

│ │ ├── *Service.java (interfaces)

│ │ └── *ServiceImpl.java

│ ├── exception/ ← Custom exceptions

│ │ └── InsufficientStockException.java

│ ├── Store.java ← Central orchestrator (Facade)

│ └── Main.java ← Simple demo runner

├── test/

│ └── store/

│ ├── model/ ← Unit tests for entities

│ ├── service/ ← Unit tests for services

│ └── integration/ ← Integration tests (StoreTest)

└── receipts/ ← Generated receipt files (git‑ignored)

---

## 🧱 Architecture & Design

The application follows a **Layered Architecture** combined with the **Facade Pattern**


- **Model Layer**: Immutable where possible, entities know only about themselves.
- **Service Layer**: Each service has a single responsibility (e.g., `PricingServiceImpl` only calculates prices).
- **Store (Facade)**: Provides a simplified interface to the complex subsystem, coordinates transactions.

**Dependency Inversion**: `Store` depends on abstractions (interfaces like `PricingService`), not concrete implementations. This enables easy switching of implementations (e.g., mock services for testing).

---

## 💡 SOLID Principles & Best Practices

### ✅ Single Responsibility Principle (SRP)
Every class has exactly one reason to change:
- `Receipt` holds data, `ReceiptServiceImpl` manages receipt storage, `ReceiptFileServiceImpl` handles file I/O.

### ✅ Open/Closed Principle (OCP)
New pricing strategies can be added by implementing `PricingService` without modifying `Store`.

### ✅ Liskov Substitution Principle (LSP)
All service implementations are interchangeable through their interfaces (e.g., `InventoryServiceImpl` can be replaced with a database version).

### ✅ Interface Segregation Principle (ISP)
Interfaces are small and focused (`ReceiptFileService` only exposes `saveReceipt`, not unrelated methods).

### ✅ Dependency Inversion Principle (DIP)
High‑level module `Store` receives low‑level modules via constructor injection, never instantiates them directly.

### Other Highlights:
- **Immutability**: `ReceiptItem` takes a snapshot of product data, making receipts historically accurate.
- **Encapsulation**: Collections returned as unmodifiable copies (`List.copyOf()`).
- **Fail‑Fast Validation**: All constructors validate input immediately, throwing `IllegalArgumentException`.
- **Try‑with‑resources**: Automatic resource management for file writing.
- **Custom Exceptions**: `InsufficientStockException` carries detailed context (product ID, requested vs available).

---

## 🧪 Testing Strategy

| Test Type        | Coverage                                      | Highlights                              |
|------------------|-----------------------------------------------|-----------------------------------------|
| **Unit Tests**   | Models, services in isolation                 | 50+ test methods                        |
| **Integration**  | `StoreTest` – real services wired together    | Uses `@TempDir` to test file output     |

**Example test scenarios:**
- Product creation with invalid data (negative price, empty name).
- Pricing: normal, near expiration, exactly on threshold, expired product throws exception.
- Inventory: insufficient stock throws `InsufficientStockException`.
- Receipt lifecycle: creation, items addition, total calculation, ID uniqueness.
- File I/O: verify receipt file content and existence.
- Register assignment: prevent two cashiers on same register.

**Testing Tools:** JUnit 5, `@TempDir` for temporary file systems, `assertThrows` for exception validation.

---

## ⚠️ Known Issues & TODOs

> These items are acknowledged and planned for future improvements.

- [ ] **Monetary precision**: `double` is used for prices, causing floating‑point artifacts (e.g., `15.283999...`).  
  *Solution:* Replace with `BigDecimal` for exact arithmetic.
- [ ] **No rollback on payment failure**: Stock is reduced when adding items to receipt; if the customer cannot pay, inventory remains reduced.  
  *Plan:* Implement transaction‑like behaviour or delay stock reduction until payment.
- [ ] **Unassign from register not used**: `unassignFromRegister()` exists but is never called from `Store`.  
  *Plan:* Integrate it when a cashier is moved to another register.
- [ ] **Deserialization missing**: Receipts can be saved to files, but there is no method to read them back into objects.
- [ ] **Salary costs not in financial report**: `Store.printFinancialReport()` omits cashier salaries. Add `getTotalSalaryCosts()` and profit calculation.
- [ ] **Lack of persistent storage**: Service data is kept only in memory (using `ArrayList`/`HashMap`). Could be replaced with a database.

---

## 🚀 Getting Started

### Prerequisites
- **JDK 17** or later
- Git (for cloning)

### Clone & Run
```bash
git clone https://github.com/your-username/store-management-system.git
cd store-management-system

# Compile all Java files
javac -d out $(find src/main -name "*.java")

# Run the demo
java -cp out store.demo.StoreDemo

# Compile tests (adjust classpath to include JUnit 5 jars or use an IDE)
javac -d out -cp out:lib/junit-platform-console-standalone-1.10.0.jar $(find src/test -name "*.java")

# Execute tests
java -jar lib/junit-platform-console-standalone-1.10.0.jar --class-path out --scan-class-path
```

(For simplicity, an IDE like IntelliJ IDEA is recommended to run tests graphically.)

--------

## 👤 Author & Contact

Maxim Ivanov – Java Developer

    GitHub: @MaximSimeonovIvanov

    Email: maksimsimeonov@tutamail.com

This project was developed as part of a university coursework in software engineering, demonstrating modern Java development practices, clean code principles, and thorough testing.