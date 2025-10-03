# E-Commerce Microservices with Kafka Implementation

## Overview
This project now includes a complete Kafka event-driven architecture that enables asynchronous communication between microservices.

## Kafka Topics and Event Flow

### 1. Order Events
- **order-created**: Published when a new order is placed
- **order-updated**: Published when order status changes
- **order-cancelled**: Published when order is cancelled

### 2. Payment Events
- **payment-initiated**: Published when payment process starts
- **payment-completed**: Published when payment succeeds
- **payment-failed**: Published when payment fails

### 3. Notification Events
- **notification-send**: Published to send emails/notifications

### 4. User Events
- **user-created**: Published when new user registers
- **user-updated**: Published when user profile changes

## Event Flow Architecture

```
Order Service → payment-initiated → Payment Service
Payment Service → payment-completed/failed → Order Service
Order Service → notification-send → Notification Service
User Service → user-created → Notification Service
```

## Services Configuration

### Order Service (Port: 8083)
- Publishes: Order events, Payment initiation, Notifications
- Consumes: Payment completion/failure events

### Payment Service (Port: 8082)
- Publishes: Payment completion/failure events
- Consumes: Payment initiation events

### User Service (Port: 8081)
- Publishes: User creation/update events
- Primarily a producer service

### Notification Service (Port: 8084)
- Consumes: All notification events
- Sends emails using Spring Mail

## Setup Instructions

### 1. Start Kafka Infrastructure
```bash
docker-compose up -d
```

### 2. Verify Kafka is Running
- Kafka: http://localhost:9092
- Kafka UI: http://localhost:8090
- MySQL: localhost:3306

### 3. Create Databases
Connect to MySQL and create databases:
- order_service_db
- payment_service_db
- user_service_db

### 4. Start Services
1. Start Eureka Server (service-discovery)
2. Start all microservices

## Key Features Implemented

1. **Event-Driven Communication**: Services communicate via Kafka events
2. **Fault Tolerance**: Kafka provides durability and replay capability
3. **Scalability**: Easy to add new consumers for existing events
4. **Decoupling**: Services are loosely coupled through events
5. **Monitoring**: Kafka UI for monitoring topics and messages

## Configuration Files
All services now include Kafka configuration in their application.properties files with proper serialization and consumer group settings.
