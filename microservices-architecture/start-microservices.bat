@echo off
echo Starting E-Commerce Microservices...
echo.

echo Starting Eureka Server (Service Discovery)...
cd service-discovery
start "Eureka Server" cmd /k "mvn spring-boot:run"
echo Waiting for Eureka Server to start...
timeout /t 30 /nobreak >nul
cd ..

echo Starting API Gateway...
cd api-gateway
start "API Gateway" cmd /k "mvn spring-boot:run"
echo Waiting for API Gateway to start...
timeout /t 20 /nobreak >nul
cd ..

echo Starting User Service...
cd user-service
start "User Service" cmd /k "mvn spring-boot:run"
timeout /t 15 /nobreak >nul
cd ..

echo Starting Product Service...
cd product-service
start "Product Service" cmd /k "mvn spring-boot:run"
timeout /t 15 /nobreak >nul
cd ..

echo Starting Order Service...
cd order-service
start "Order Service" cmd /k "mvn spring-boot:run"
timeout /t 15 /nobreak >nul
cd ..

echo Starting Payment Service...
cd payment-service
start "Payment Service" cmd /k "mvn spring-boot:run"
timeout /t 15 /nobreak >nul
cd ..

echo Starting Notification Service...
cd notification-service
start "Notification Service" cmd /k "mvn spring-boot:run"
timeout /t 15 /nobreak >nul
cd ..

echo.
echo All microservices are starting up...
echo Check individual windows for startup status.
echo API Gateway will be available at: http://localhost:8080
echo Eureka Dashboard: http://localhost:8761
pause
