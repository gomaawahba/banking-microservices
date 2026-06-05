🏦 Banking Microservices — Spring Boot
مشروع Microservices كامل لنظام بنكي باستخدام Spring Boot 3 و Spring Cloud.

📐 Architecture
Client
  │
  ▼ HTTPS
API Gateway (8080)
  ├── JWT Validation
  ├── Rate Limiting
  └── Circuit Breaker
       │
       ├──► Auth Service (8081)       → PostgreSQL (auth_db)
       ├──► User Service (8082)       → PostgreSQL (user_db)
       ├──► Transaction Service (8083) → PostgreSQL (transaction_db)
       └──► Notification Service (8084) → PostgreSQL (notification_db)
                                              ▲
                          Kafka (banking.transactions) ──┘

Infrastructure:
  ├── Config Server (8888) — Centralized Config
  └── Eureka Server (8761) — Service Discovery
🚀 تشغيل المشروع
طريقة 1: Docker Compose (الأسهل)
# تشغيل كل حاجة
docker compose up -d

# مراقبة الـ logs
docker compose logs -f

# إيقاف كل حاجة
docker compose down

# مع حذف الـ volumes (قاعدة البيانات)
docker compose down -v
طريقة 2: Manual (للـ Development)
# 1. شغّل PostgreSQL و Kafka
docker compose up -d postgres-auth postgres-user postgres-transaction postgres-notification zookeeper kafka

# 2. Config Server
cd config-server && mvn spring-boot:run

# 3. Eureka Server
cd eureka-server && mvn spring-boot:run

# 4. Auth Service
cd auth-service && mvn spring-boot:run

# 5. User Service
cd user-service && mvn spring-boot:run

# 6. Transaction Service
cd transaction-service && mvn spring-boot:run

# 7. Notification Service
cd notification-service && mvn spring-boot:run

# 8. API Gateway
cd api-gateway && mvn spring-boot:run
🧪 اختبار الـ API
Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "أحمد محمد",
    "email": "ahmed@example.com",
    "password": "Ahmed@1234",
    "nationalId": "1234567890"
  }'
Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmed@example.com",
    "password": "Ahmed@1234"
  }'
# احتفظ بالـ accessToken من الـ response
فتح حساب
curl -X POST http://localhost:8080/api/v1/users/accounts?type=CHECKING \
  -H "Authorization: Bearer YOUR_TOKEN"
تحويل مبلغ
curl -X POST http://localhost:8080/api/v1/transactions/transfer \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "FROM_ACCOUNT_UUID",
    "toAccountId": "TO_ACCOUNT_UUID",
    "amount": 500.00,
    "description": "تحويل شخصي"
  }'
سجل المعاملات
curl http://localhost:8080/api/v1/transactions/history \
  -H "Authorization: Bearer YOUR_TOKEN"
📊 Dashboards & Monitoring
Service	URL	Credentials
Eureka Dashboard	http://localhost:8761	eureka-admin / eureka-secret
Config Server	http://localhost:8888	config-admin / config-secret-123
Kafka UI (tools)	http://localhost:8090	-
API Gateway Actuator	http://localhost:8080/actuator	-
🔐 Environment Variables (Production)
JWT_SECRET=your-super-secret-key-min-256-bits-base64-encoded
CONFIG_SERVER_PASSWORD=strong-password
EUREKA_PASSWORD=strong-password
DB_PASSWORD=strong-db-password
MAIL_HOST=smtp.your-provider.com
MAIL_USERNAME=noreply@yourdomain.com
MAIL_PASSWORD=mail-app-password
📁 Project Structure
banking-microservices/
├── config-server/          ← Centralized Config (port 8888)
├── eureka-server/          ← Service Discovery (port 8761)
├── auth-service/           ← JWT Auth + Spring Security (port 8081)
│   ├── entity/User.java
│   ├── entity/RefreshToken.java
│   ├── service/JwtService.java
│   ├── service/AuthService.java
│   ├── filter/JwtAuthenticationFilter.java
│   └── controller/AuthController.java
├── user-service/           ← KYC + Accounts (port 8082)
├── transaction-service/    ← Payments + Kafka Producer (port 8083)
├── notification-service/   ← Kafka Consumer + Email (port 8084)
├── api-gateway/            ← Spring Cloud Gateway (port 8080)
└── docker-compose.yml
🔄 Request Flow
1. Client → POST /api/v1/transactions/transfer
2. API Gateway → JWT filter يتحقق من التوكن
3. Gateway → يضيف X-User-Id في header
4. Gateway → يوجّه الـ request لـ Transaction Service
5. Transaction Service → يحفظ العملية في DB
6. Transaction Service → يبعت event على Kafka
7. Notification Service → يستهلك الـ event
8. Notification Service → يبعت إيميل للمستخدم
⚙️ Tech Stack
Component	Technology
Framework	Spring Boot 3.2.5
Service Discovery	Spring Cloud Netflix Eureka
Config	Spring Cloud Config Server
Gateway	Spring Cloud Gateway
Auth	Spring Security + JWT (JJWT 0.12.5)
Messaging	Apache Kafka
Database	PostgreSQL 15
Migrations	Flyway
Containers	Docker + Docker Compose
Java	Java 17
