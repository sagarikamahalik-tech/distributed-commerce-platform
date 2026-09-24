# Distributed Commerce Platform

A hands-on project for learning Java, Spring Boot, REST APIs, gRPC, testing, and microservices.

## Tech Stack

- Java 21
- Spring Boot
- Maven
- JUnit 5
- Mockito
- Spring MockMvc

## Services

### Order Service

The Order Service currently supports:

- Creating orders with server-side product pricing
- Retrieving orders by ID
- Request validation
- Centralized exception handling
- In-memory order storage
- Unit, controller, and integration tests

## Running the Order Service

From the `order-service` directory:

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs on port 8080 by default.

## Running Tests

```powershell
.\mvnw.cmd clean test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/orders | Create an order |
| GET | /api/v1/orders/{orderId} | Retrieve an order |

## Planned Improvements

- PostgreSQL persistence
- gRPC communication between services
- Docker and Kubernetes
- AWS deployment
```