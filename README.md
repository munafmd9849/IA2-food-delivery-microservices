# IA2-food-delivery-microservices


# Online Food Ordering & Delivery Management System

A distributed **Online Food Ordering & Delivery Management System** built using **Spring Boot Microservices**.

The project demonstrates centralized configuration, JWT authentication and RBAC, service discovery, API Gateway routing, synchronous inter-service communication with OpenFeign, Redis caching, asynchronous event processing with Apache Kafka, and independent database ownership.

---

## Architecture

```text
                              ┌──────────────┐
                              │    CLIENT    │
                              └──────┬───────┘
                                     │
                                     ▼
                         ┌─────────────────────┐
                         │     API GATEWAY     │
                         │       :8080         │
                         └─────────┬───────────┘
                                   │
                ┌──────────────────┼──────────────────┐
                │                  │                  │
                ▼                  ▼                  ▼
        ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
        │ USER-SERVICE │   │ FOOD-SERVICE │   │ ORDER-SERVICE│
        │    :8081     │   │    :8082     │   │    :8083     │
        └──────┬───────┘   └──────┬───────┘   └──────┬───────┘
               │                  │                  │
               ▼                  ▼                  │
        ┌──────────────┐   ┌──────────────┐         │
        │    MySQL     │   │    MySQL     │         │
        │ food_delivery│   │ food_delivery│         │
        │     _user    │   │     _food    │         │
        └──────────────┘   └──────┬───────┘         │
                                  │                  │
                                  ▼                  │
                           ┌──────────────┐          │
                           │    Redis     │          │
                           │    :6379     │          │
                           └──────────────┘          │
                                                     │
                                  OpenFeign           │
                              ┌────────┴────────┐     │
                              ▼                 ▼     │
                       ┌──────────────┐  ┌──────────────┐
                       │FOOD-SERVICE  │  │PAYMENT-SERVICE│
                       │              │  │    :8084      │
                       └──────────────┘  └──────┬────────┘
                                                │
                                                ▼
                                         ┌──────────────┐
                                         │    MySQL     │
                                         │ food_delivery│
                                         │   _payment   │
                                         └──────────────┘

                       ORDER-SERVICE
                            │
                            │ publish
                            ▼
                    ┌─────────────────┐
                    │ Apache Kafka    │
                    │    :9092        │
                    │ order-created   │
                    └────────┬────────┘
                             │ consume
                             ▼
                    ┌────────────────────┐
                    │ NOTIFICATION-SERVICE│
                    │       :8085        │
                    └────────────────────┘

             ┌──────────────────────┐
             │   EUREKA-SERVER      │
             │       :8761          │
             │ Service Discovery    │
             └──────────────────────┘

             ┌──────────────────────┐
             │   CONFIG-SERVER      │
             │       :8888          │
             │ Git-backed Config    │
             └──────────────────────┘
```

### Request flow

```text
Client
  │
  ▼
API Gateway
  │
  ├── /api/users/**   ──────► USER-SERVICE
  ├── /api/foods/**   ──────► FOOD-SERVICE
  ├── /api/orders/**  ──────► ORDER-SERVICE
  └── /api/payments/**──────► PAYMENT-SERVICE

ORDER-SERVICE
  │
  ├── OpenFeign ────────────► FOOD-SERVICE
  │                            └─ availability + price
  │
  ├── OpenFeign ────────────► PAYMENT-SERVICE
  │                            └─ payment processing
  │
  ├── Save order ───────────► Order MySQL
  │
  └── Kafka ────────────────► order-created
                               │
                               ▼
                         NOTIFICATION-SERVICE
```

---

## Microservices

| Service | Port | Responsibility |
|---|---:|---|
| EUREKA-SERVER | `8761` | Service discovery |
| CONFIG-SERVER | `8888` | Centralized configuration |
| API-GATEWAY | `8080` | Common entry point and routing |
| USER-SERVICE | `8081` | Registration, login, users, JWT |
| FOOD-SERVICE | `8082` | Restaurants, food and Redis caching |
| ORDER-SERVICE | `8083` | Orders, Feign calls and Kafka producer |
| PAYMENT-SERVICE | `8084` | Simulated payment processing |
| NOTIFICATION-SERVICE | `8085` | Kafka consumer and order notification |

---

## Technology Stack

- Java 21
- Spring Boot 4.1.1
- Spring Cloud
- Spring Cloud Config Server
- Spring Cloud Gateway
- Netflix Eureka
- Spring Cloud OpenFeign
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- MySQL
- Redis
- Apache Kafka
- Maven
- Lombok
- Springdoc OpenAPI / Swagger

---

## Key Features

### 1. Centralized Configuration

All application configuration is maintained in a separate Git-backed Config Server.

Repository:

`IA2-config-repo-microservice`

The Config Server runs on:

`http://localhost:8888`

Example configuration responsibilities:

- Database connection
- Server ports
- Kafka configuration
- Redis configuration
- JPA configuration
- JWT secret
- Eureka configuration

Individual services import configuration using:

```yaml
spring:
  application:
    name: USER-SERVICE
  config:
    import: configserver:http://localhost:8888
```

---

### 2. JWT Authentication & RBAC

The User Service authenticates users using Spring Security.

Authentication flow:

```text
Email + Password
      │
      ▼
User Service
      │
      ▼
AuthenticationManager
      │
      ▼
UserDetailsService
      │
      ▼
MySQL
      │
      ▼
JWT Token
```

The token is then supplied with protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

Roles:

- `CUSTOMER`
- `ADMIN`

Example authorization:

| Operation | CUSTOMER | ADMIN |
|---|:---:|:---:|
| Register | ✅ | ✅ |
| Login | ✅ | ✅ |
| View food | ✅ | ✅ |
| Add food | ❌ | ✅ |
| Update food | ❌ | ✅ |
| Delete food | ❌ | ✅ |
| Place order | ✅ | ✅ |
| View own orders | ✅ | ✅ |
| Manage orders | ❌/restricted | ✅ |

Unauthorized role access should return:

```text
HTTP 403 Forbidden
```

---

## API Gateway Routes

The client should normally access the application through:

```text
http://localhost:8080
```

Routes:

| Gateway Path | Destination |
|---|---|
| `/api/users/**` | USER-SERVICE |
| `/api/foods/**` | FOOD-SERVICE |
| `/api/orders/**` | ORDER-SERVICE |
| `/api/payments/**` | PAYMENT-SERVICE |

The Gateway uses Eureka service discovery with:

```text
lb://USER-SERVICE
lb://FOOD-SERVICE
lb://ORDER-SERVICE
lb://PAYMENT-SERVICE
```

---

## User Service

Base URL:

```text
http://localhost:8081
```

Main operations:

```http
POST   /api/users/register
POST   /api/users/login
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

User fields:

```text
customerId
name
email
password
phone
role
```

New users are registered as `CUSTOMER`.

An administrator can be configured with the `ADMIN` role.

---

## Food Service

Base URL:

```text
http://localhost:8082
```

Main operations:

```http
POST   /api/foods
GET    /api/foods/{id}
GET    /api/foods
PUT    /api/foods/{id}
DELETE /api/foods/{id}
```

Restaurant operations:

```http
POST   /api/restaurants
GET    /api/restaurants/{id}
GET    /api/restaurants
PUT    /api/restaurants/{id}
DELETE /api/restaurants/{id}
```

Only administrators can create, update or delete food and restaurant data.

Customers can view food.

### Redis caching

Food details are cached using Spring Cache + Redis.

```text
First request
    │
    ▼
Food Service
    │
    ▼
Redis
    │
    └── Cache MISS
            │
            ▼
          MySQL
            │
            ▼
          Redis
            │
            ▼
         Response
```

Subsequent request:

```text
Food Service
    │
    ▼
Redis
    │
    └── Cache HIT
            │
            ▼
         Response
```

Food create/update/delete operations evict the relevant cache entries to prevent stale data.

---

## Order Service

Base URL:

```text
http://localhost:8083
```

Main operations:

```http
POST /api/orders
GET  /api/orders/{orderId}
GET  /api/orders/customer/{customerId}
PUT  /api/orders/{orderId}/cancel
```

The Order Service owns the order database and does not directly access the Food or Payment databases.

### OpenFeign

When an order is created:

```text
ORDER-SERVICE
     │
     ├── Feign ──► FOOD-SERVICE
     │              └─ Check availability + get price
     │
     └── Feign ──► PAYMENT-SERVICE
                    └─ Process payment
```

The authorization header is propagated between services.

---

## Payment Service

Base URL:

```text
http://localhost:8084
```

Endpoint:

```http
POST /api/payments
```

Example:

```json
{
  "orderId": 1,
  "amount": 250.0
}
```

The payment service simulates payment processing.

Possible results:

```text
SUCCESS
FAILED
```

---

## Kafka & Notification Service

Kafka:

```text
localhost:9092
```

Topic:

```text
order-created
```

After an order is successfully created:

```text
ORDER-SERVICE
      │
      │ publish event
      ▼
Kafka: order-created
      │
      │ consume
      ▼
NOTIFICATION-SERVICE
      │
      ▼
Order confirmation
```

The Order Service does **not** directly call the Notification Service.

Example event:

```json
{
  "orderId": 101,
  "customerId": 10,
  "totalAmount": 850.0,
  "status": "PLACED"
}
```

The Notification Service consumes the event asynchronously and prints a simulated confirmation.

---

## Database Ownership

Each major microservice owns its own database/schema.

```text
USER-SERVICE
    └── food_delivery_user

FOOD-SERVICE
    └── food_delivery_food

ORDER-SERVICE
    └── food_delivery_order

PAYMENT-SERVICE
    └── food_delivery_payment
```

There is no direct database access between microservices.

Inter-service communication is performed using APIs/OpenFeign.

---

## Complete Business Flow

```text
1. Customer Registration
        │
        ▼
   USER-SERVICE
        │
        ▼
      MySQL

2. Customer Login
        │
        ▼
   Spring Security
        │
        ▼
      JWT

3. Browse Food
        │
        ▼
   API Gateway
        │
        ▼
   FOOD-SERVICE
        │
        ▼
      Redis
     /     \
  HIT       MISS
             │
             ▼
           MySQL

4. Place Order
        │
        ▼
   ORDER-SERVICE
       / \
      /   \
   Feign  Feign
    /       \
   ▼         ▼
 FOOD      PAYMENT
   │         │
   └────┬────┘
        ▼
   Save Order

5. Publish Event
        │
        ▼
      Kafka
   order-created
        │
        ▼
 NOTIFICATION-SERVICE
        │
        ▼
 Confirmation
```

---

## Running the Project

Start infrastructure first:

```text
MySQL
Redis
Kafka
```

Then start the Spring Boot applications in this order:

```text
1. EUREKA-SERVER       :8761
2. CONFIG-SERVER       :8888
3. USER-SERVICE        :8081
4. FOOD-SERVICE        :8082
5. PAYMENT-SERVICE     :8084
6. ORDER-SERVICE       :8083
7. NOTIFICATION-SERVICE:8085
8. API-GATEWAY         :8080
```

### Eureka Dashboard

```text
http://localhost:8761
```

### Config Server

Example:

```text
http://localhost:8888/USER-SERVICE/default
http://localhost:8888/FOOD-SERVICE/default
http://localhost:8888/ORDER-SERVICE/default
```

### Swagger

User Service:

```text
http://localhost:8081/swagger-ui/index.html
```

Food Service:

```text
http://localhost:8082/swagger-ui/index.html
```

Order Service:

```text
http://localhost:8083/swagger-ui/index.html
```

Payment Service:

```text
http://localhost:8084/swagger-ui/index.html
```

---

## Kafka Commands

Start Kafka:

```bash
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```

List topics:

```bash
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list
```

Create the order topic if required:

```bash
/opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic order-created \
  --partitions 1 \
  --replication-factor 1
```

---

## Demo / Evaluation Checklist

The following should be demonstrated during evaluation:

- [x] Config Server loads centralized configuration
- [x] Eureka service discovery
- [x] API Gateway routing
- [x] JWT authentication
- [x] CUSTOMER / ADMIN role-based authorization
- [x] HTTP 403 for unauthorized role
- [x] Independent databases
- [x] OpenFeign Order → Food
- [x] OpenFeign Order → Payment
- [x] Redis cache hit
- [x] Redis cache miss
- [x] Redis cache eviction after food update/delete
- [x] Kafka producer
- [x] `order-created` Kafka topic
- [x] Kafka consumer
- [x] Notification Service
- [x] Complete order → payment → Kafka → notification flow

---

## Project Structure

```text
IA2JAVA-food-delivery-microservices/
│
├── eureka-server/
├── config-server/
├── api-gateway/
├── user-service/
├── food-service/
├── order-service/
├── payment-service/
└── notification-service/
```

Configuration is maintained separately in:

```text
IA2-config-repo-microservice/
│
├── application.yml
├── API-GATEWAY.yml
├── USER-SERVICE.yml
├── FOOD-SERVICE.yml
├── ORDER-SERVICE.yml
├── PAYMENT-SERVICE.yml
└── NOTIFICATION-SERVICE.yml
```

---

## Important Security Note

For a classroom/local demonstration, configuration currently uses local credentials and a JWT secret.

For production, do **not** commit real:

- Database passwords
- JWT secrets
- API keys
- Cloud credentials

Use environment variables, secret management, or a private configuration repository.

---

## Future Improvements

- Centralized JWT validation at the Gateway
- Refresh tokens
- Docker Compose for infrastructure
- Global exception handling
- API validation
- Distributed tracing
- Circuit breaker / Resilience4j
- Production-grade secrets management
- Real payment gateway integration
- Email/SMS notification integration
- Pagination and filtering
