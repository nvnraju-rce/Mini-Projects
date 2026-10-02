# Mini Shop - E-Commerce with Mock Payment Gateway

Spring Boot 3 | Java 17 | REST | JPA + H2 | simple HTML/JS UI. No real money is processed.

## Run in Eclipse
1. Install JDK 17+ and use "Eclipse IDE for Enterprise Java and Web Developers" (Maven support included).
2. Unzip, then File > Import > Maven > Existing Maven Projects > select the `mini-shop` folder > Finish.
   Wait for Maven to download dependencies (internet needed the first time).
3. Right-click `ShopApplication.java` > Run As > Java Application.
4. Open http://localhost:8080 and log in with `student / student123`.

(Command line alternative: `mvn spring-boot:run`)

## Test cards (mock gateway)
| Card | Result |
|---|---|
| 4111 1111 1111 1111 | SUCCESS |
| 4000 0000 0000 0002 | FAILED - card declined |
| 4000 0000 0000 9995 | FAILED - insufficient funds |
| 4000 0000 0000 0119 | Slow gateway, shop times out after 3 s -> PAYMENT_PENDING |
| anything else | FAILED - invalid card |

Failed or pending orders can be retried from "My Orders". A PAID order is never charged twice (idempotency).

## 13-step flow in code
| Step | Where |
|---|---|
| 1-2 Select products, add to cart | `CartController`, `CartService.add` |
| 3-6 Checkout, total, create order, order id | `OrderService.checkout` |
| 7-8 Call mock gateway and its response | `PaymentService.charge`, `GatewayController` |
| 9-10 Receive and validate response | `PaymentService.charge` |
| 11-12 Store payment, update order status | `OrderService.charge` |
| 13 Receipt | `OrderService.receipt`, UI receipt panel |

## REST API
- POST `/api/auth/login`
- GET `/api/products`
- GET, POST, DELETE `/api/cart`
- POST `/api/orders/checkout`, GET `/api/orders`, GET `/api/orders/{no}/receipt`, POST `/api/orders/{no}/pay`
- POST `/gateway/pay` (mock gateway)

Send the token from login in the `X-Auth-Token` header. Errors return JSON: `{"error": "..."}`.

Database console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:shopdb`, user `sa`, empty password).

## Deploy
`mvn clean package`, then run `java -jar target/mini-shop-1.0.0.jar`, or build the Docker image with the included Dockerfile.
The app reads the `PORT` environment variable, so it works on most cloud platforms.

## Student exercises
1. Replace public entity fields with private fields, getters and setters.
2. Replace the token login with Spring Security and JWT.
3. Move the gateway into a second Spring Boot project (port 8081) and change `gateway.url`.
4. Switch H2 to PostgreSQL or MySQL.
5. Add unit tests for `OrderService` and `GatewayController`.
6. Discuss: why is the payment call inside `@Transactional`? What can go wrong on a PENDING timeout?
