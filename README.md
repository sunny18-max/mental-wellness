# Event-Driven Microservices E-Commerce Platform

Simplified Amazon-style backend built as independent Spring Boot services.

## Services

| Service | Port | Datastore | Responsibility |
|---|---|---|---|
| customer-service | 8081 | PostgreSQL | Registration, login, JWT auth |
| product-service | 8082 | PostgreSQL | Product catalog, search, soft delete |
| inventory-service | 8083 | PostgreSQL | Stock levels, reservation/release |
| order-service | 8084 | PostgreSQL | Order creation, calls inventory-service |
| payment-service | 8085 | PostgreSQL | Payment charge simulation |
| notification-service | 8086 | MongoDB | Email notifications + logs |

## Run infra

```
docker-compose up -d
```

Postgres is exposed on host port **5544** (not 5432) to avoid clashing with any
native PostgreSQL install already on the machine. Mongo is on the standard 27017.

## Run a service

```
cd customer-service
mvn spring-boot:run
```

Swagger UI (customer/product-service): `http://localhost:<port>/swagger-ui.html`

## Flow

1. `POST /api/v1/auth/register` on customer-service → JWT
2. `POST /api/v1/products` on product-service → create product
3. `POST /api/v1/inventory/{productId}/stock?quantity=N` → stock it
4. `POST /api/v1/orders` on order-service → reserves inventory, creates order
5. `POST /api/v1/payments/charge` on payment-service → simulate payment
6. `POST /api/v1/notifications/email` on notification-service → send confirmation

## Next steps

- Add Docker images + Dockerfiles per service, deploy to AWS EC2
- Add rate limiting, caching (Redis), centralized logging
- Wire an event bus (Kafka/RabbitMQ) between order → inventory → payment → notification instead of direct REST calls
