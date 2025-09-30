@echo off
echo Starting E-Commerce Microservices Backend...
echo.

echo ====================================
echo Starting Service Discovery (Eureka Server)...
echo ====================================
start "Service Discovery" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\service-discovery && mvn spring-boot:run"

echo Waiting 15 seconds for Eureka Server to start...
timeout /t 15 /nobreak

echo ====================================
echo Starting API Gateway...
echo ====================================
start "API Gateway" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\api-gateway && mvn spring-boot:run"

echo Waiting 10 seconds for API Gateway to start...
timeout /t 10 /nobreak

echo ====================================
echo Starting User Service...
echo ====================================
start "User Service" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\user-service && mvn spring-boot:run"

echo ====================================
echo Starting Product Service...
echo ====================================
start "Product Service" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\product-service && mvn spring-boot:run"

echo ====================================
echo Starting Order Service...
echo ====================================
start "Order Service" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\order-service && mvn spring-boot:run"

echo ====================================
echo Starting Payment Service...
echo ====================================
start "Payment Service" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\payment-service && mvn spring-boot:run"

echo ====================================
echo Starting Notification Service...
echo ====================================
start "Notification Service" cmd /k "cd /d F:\Desktop\Desktop\E-Commerce\E-Commerce-Final-Project-By-Springboot-React\microservices-architecture\notification-service && mvn spring-boot:run"

echo.
echo ====================================
echo All microservices are starting up...
echo ====================================
echo Service URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - User Service: http://localhost:8081
echo - Product Service: http://localhost:8082
echo - Order Service: http://localhost:8083
echo - Payment Service: http://localhost:8084
echo - Notification Service: http://localhost:8085
echo.
echo Press any key to exit...
pause
