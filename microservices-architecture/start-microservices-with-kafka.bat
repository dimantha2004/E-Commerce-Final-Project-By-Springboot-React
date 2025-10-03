@echo off
echo Starting E-Commerce Microservices with Kafka...

echo.
echo ========================================
echo Starting Kafka Infrastructure...
echo ========================================
docker-compose up -d

echo Waiting for Kafka to be ready...
timeout /t 30 /nobreak > nul

echo.
echo ========================================
echo Starting Eureka Server...
echo ========================================
cd service-discovery
start "Eureka Server" cmd /k "mvn spring-boot:run"
cd ..

echo Waiting for Eureka Server to start...
timeout /t 20 /nobreak > nul

echo.
echo ========================================
echo Starting User Service...
echo ========================================
cd user-service
start "User Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo Starting Payment Service...
echo ========================================
cd payment-service
start "Payment Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo Starting Order Service...
echo ========================================
cd order-service
start "Order Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo Starting Notification Service...
echo ========================================
cd notification-service
start "Notification Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo Starting Product Service...
echo ========================================
cd product-service
start "Product Service" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo Starting API Gateway...
echo ========================================
cd api-gateway
start "API Gateway" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo ========================================
echo All services are starting...
echo ========================================
echo Eureka Server: http://localhost:8761
echo User Service: http://localhost:8081
echo Payment Service: http://localhost:8082
echo Order Service: http://localhost:8083
echo Notification Service: http://localhost:8084
echo Product Service: http://localhost:8085
echo API Gateway: http://localhost:8080
echo Kafka UI: http://localhost:8090
echo ========================================

pause
