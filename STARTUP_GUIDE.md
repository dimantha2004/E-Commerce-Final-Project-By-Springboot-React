# E-Commerce Microservices Startup Guide

## Prerequisites
1. **MySQL Database**: Ensure MySQL is running on localhost:3306 with database named `ecom`
2. **Kafka**: Start Kafka server on localhost:9092 (optional, but recommended)
3. **Java 17+**: Ensure you have Java 17 or higher installed
4. **Maven**: Make sure Maven is installed and available in PATH

## Service Ports
- **Eureka Server (Service Discovery)**: 8761
- **API Gateway**: 8080
- **User Service**: 8081
- **Product Service**: 8082
- **Order Service**: 8083
- **Payment Service**: 8084
- **Notification Service**: 8085

## Startup Steps

### 1. Database Setup
```sql
CREATE DATABASE IF NOT EXISTS ecom;
```

### 2. Start Services (in order)

#### Method 1: Using the Batch File
```bash
cd microservices-architecture
start-microservices.bat
```

#### Method 2: Manual Startup
1. **Start Eureka Server**
   ```bash
   cd service-discovery
   mvn spring-boot:run
   ```
   Wait for it to start (check http://localhost:8761)

2. **Start API Gateway**
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

3. **Start Individual Services** (can be started in parallel after Gateway is up)
   ```bash
   # User Service
   cd user-service
   mvn spring-boot:run
   
   # Product Service
   cd product-service
   mvn spring-boot:run
   
   # Order Service
   cd order-service
   mvn spring-boot:run
   
   # Payment Service
   cd payment-service
   mvn spring-boot:run
   
   # Notification Service
   cd notification-service
   mvn spring-boot:run
   ```

### 3. Start Frontend
```bash
cd e-commerce-frontend
npm install
npm start
```

## API Endpoints (through API Gateway at http://localhost:8080)

### Authentication & Users
- POST `/api/auth/register` - User registration
- POST `/api/auth/login` - User login
- POST `/api/auth/google` - Google OAuth login
- GET `/api/users/my-info` - Get current user info

### Products & Categories
- GET `/api/categories/get-all` - Get all categories
- POST `/api/categories/create` - Create category (Admin)
- GET `/api/products/get-all` - Get all products
- POST `/api/products/create` - Create product (Admin)

### Orders
- POST `/api/orders/create` - Create order
- GET `/api/orders/filter` - Get orders

### Payments
- POST `/api/payments/create-checkout-session` - Create payment session

### Notifications
- POST `/api/notifications/send-status-email` - Send status email

## Monitoring & Health Checks
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080/actuator/health
- **Frontend**: http://localhost:3000

## Configuration Notes
- All services use the same database: `ecom`
- JWT secret is synchronized across all services
- Kafka is configured for inter-service communication
- Google OAuth placeholder is set (replace with actual client ID)

## Troubleshooting
1. **JWT Issues**: All services now have the same JWT secret key
2. **Database Issues**: Ensure MySQL is running and `ecom` database exists
3. **Service Discovery**: Make sure Eureka is started first
4. **Port Conflicts**: Check if any ports are already in use

## Purpose of Architecture Components

### Eureka Server
- **Service Discovery**: Automatically registers and discovers microservices
- **Load Balancing**: Distributes requests across multiple instances
- **Health Monitoring**: Tracks service health and availability

### API Gateway
- **Single Entry Point**: All client requests go through one gateway
- **Routing**: Routes requests to appropriate microservices
- **Security**: Centralized authentication and authorization
- **CORS Handling**: Manages cross-origin requests

### Kafka
- **Asynchronous Communication**: Services communicate without direct coupling
- **Event-Driven Architecture**: Order events trigger notifications
- **Scalability**: Handles high-volume message processing
- **Reliability**: Ensures message delivery and ordering
