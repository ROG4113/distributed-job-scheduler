# Distributed Job Scheduler

A resilient, high-concurrency background task orchestrator built with Spring Boot and Java 25. This system is designed to manage and execute diverse job types across multiple application instances while ensuring data integrity and task reliability.

## Project Overview

In a distributed environment, ensuring that a task is executed exactly once is a significant challenge. This project implements a robust scheduling engine that utilizes database-level locking and design patterns to coordinate task execution across horizontally scaled nodes.

## Core Features

* **Distributed Task Coordination**: Prevents race conditions using pessimistic locking, ensuring only one instance claims a job at a time.
* **Extensible Worker Strategy**: Utilizes the Strategy Pattern for decoupled job execution logic, allowing for easy integration of new job types.
* **Resilient State Machine**: Manages job lifecycles (PENDING, RUNNING, SUCCESS, FAILED) with built-in retry logic and visibility timeouts.
* **Global Error Handling**: Centralized exception management to provide consistent API responses and system stability.
* **Containerized Architecture**: Fully dockerized setup for consistent deployment across different environments.

## Technical Implementation

### Concurrency and Locking
To handle high contention, the system employs **Pessimistic Locking** (`SELECT FOR UPDATE`) within the JPA repository. This prevents multiple service instances from claiming the same job simultaneously. Additionally, **Optimistic Locking** (`@Version`) is used as a secondary safeguard to maintain data integrity during state transitions.

### Strategy Pattern
The service uses a Map-based lookup for `JobWorker` implementations. This provides $O(1)$ performance when routing jobs to their respective handlers (e.g., EmailWorker) and follows the Open/Closed Principle, making the system easy to extend without modifying existing core logic.

### Fault Tolerance
Jobs that encounter transient errors (like network timeouts) are automatically rescheduled using a backoff mechanism. By updating the `scheduled_time` to a future timestamp upon failure, the system ensures that "poison pill" jobs do not immediately saturate the worker pool.

## Tech Stack

* **Language**: Java 25 (LTS)
* **Framework**: Spring Boot 3.x, Spring Data JPA
* **Database**: MySQL 8.0
* **DevOps**: Docker, Docker Compose
* **Migration**: Flyway (Database Versioning)

## Getting Started

### Prerequisites
* Docker and Docker Compose installed.

### Installation and Running
1. Clone the repository.
2. Navigate to the project root.
3. Execute the following command to build and start the entire stack:

```bash
docker-compose up --build
```

The application will be available at http://localhost:8080.

## API Reference

### Submit a Job

POST /api/v1/jobs

Request Body:
JSON

```json
{
  "jobType": "Email",
  "payload": "user@example.com",
  "scheduledInMinutes": 0
}
```

### Get Job Status

GET /api/v1/jobs/{id}

### Reset a Job

POST /api/v1/jobs/{id}/reset