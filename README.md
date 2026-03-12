# Distributed Job Scheduler (Spring Boot 3.5.11 + Java 25)

A high-performance, distributed background task orchestrator built with Spring Boot, designed for scalability and "exactly-once" execution using database-level locking.

## 🚀 Key Technical Features
- **Pessimistic Locking:** Uses `SELECT FOR UPDATE` to ensure multiple instances don't claim the same job simultaneously.
- **Optimistic Locking:** Implemented `@Version` control to prevent lost updates during high-concurrency state transitions.
- **Modern ID Management:** Uses **UUIDs** (Version 4) for distributed compatibility and performance.
- **Database Migrations:** Managed via **Flyway** for versioned, reproducible schema changes.
- **Java 25 LTS:** Leveraging the latest Long-Term Support features for enterprise reliability.

## 🏛️ Architecture & "Under the Hood"
- **Bidirectional One-to-Many:** Clean mapping between `Job` (Parent) and `JobExecution` (Audit/History).
- **Service Layer Abstraction:** Interface-based design to support loose coupling and easy mocking for Unit Tests.
- **Transactional Integrity:** Polling and claiming logic wrapped in `@Transactional` boundaries to ensure atomic state transitions.

## 🛠️ Tech Stack
- **Framework:** Spring Boot 3.5.11
- **Language:** Java 25 (LTS)
- **Database:** MySQL 8.0
- **Persistence:** Spring Data JPA / Hibernate 6
- **Build Tool:** Maven
- **Migrations:** Flyway