# 🛒 E-Commerce REST API

A full-featured e-commerce REST API built with **Spring Boot**, **Spring Security**, **JWT Authentication**, and **MySQL**.

---

## 👨‍💻 Author

| Platform   | Link |
|------------|------|
| GitHub     | [@habbashx](https://github.com/habbashx) |
| Instagram  | [@abdallah_alhabbash](https://instagram.com/abdallah_alhabbash) |

---

## 🚀 Tech Stack

| Technology        | Version  |
|-------------------|----------|
| Java              | 25       |
| Spring Boot       | 3.4.0    |
| Spring Security   | 7.x      |
| MySQL             | 8.x      |
| Hibernate / JPA   | 7.x      |
| JWT (jjwt)        | 0.11.5   |
| Lombok            | Latest   |
| Springdoc OpenAPI | 2.8.5    |
| Maven             | 3.x      |

---

## ⚙️ Setup & Installation

### 1. Clone the repository
```bash
git clone https://github.com/habbashx/e-commerce.git
cd e-commerce
```

### 2. Create the MySQL database
```sql
CREATE DATABASE ecommerce_db;
```

Then run the schema:
```bash
mysql -u root -p ecommerce_db < schema.sql
```

### 3. Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=validate

jwt.secret-key=your_secret_key_here
jwt.expiration-time=86400000

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### 4. Run the application
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## 📖 Swagger UI

Interactive API docs available at:
```
http://localhost:8080/swagger-ui.html
```

To test protected endpoints in Swagger:
1. Register or login to get a token
2. Click **Authorize** button (top right)
3. Enter: `Bearer <your_token>`
4. All requests will now include your token automatically

---

## 🔐 Authentication

This API uses **JWT Bearer Token** authentication.

After login, include the token in every protected request:
```
Authorization: Bearer <your_jwt_token>
```

---

## 📡 API Endpoints

### 🔑 Auth — `/auth`
> Public — no token required

#### Register
```http
POST /auth/register
Content-Type: application/json

{
  "username": "habbashx",
  "email": "habbashx@email.com",
  "password": "secret123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "usernameOrEmail": "habbashx",
  "password": "secret123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "login successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "username": "habbashx",
    "email": "habbashx@email.com",
    "roles": ["ROLE_USER"]
  }
}
```

---

### 📦 Products — `/products`
> GET endpoints are public. POST/PUT/DELETE require `ROLE_ADMIN`

#### Get all products (paginated)
```http
GET /products?page=0&size=10&sortBy=id&direction=asc
```

#### Get product by ID
```http
GET /products/{id}
```

#### Search products by name
```http
GET /products/search?name=laptop&page=0&size=10
```

#### Get products by category
```http
GET /products/category/{categoryId}?page=0&size=10
```

#### Filter products
```http
GET /products/filter?name=phone&categoryId=1&min=100&max=1000&page=0&size=10
```

#### Create product `🔒 ADMIN`
```http
POST /products
Authorization: Bearer <token>
Content-Type: application/json

{
  "productName": "iPhone 15",
  "productDesc": "Latest Apple smartphone",
  "price": 999.99,
  "stockQuantity": 50,
  "isActive": true,
  "categoryIds": [1, 2]
}
```

#### Update product `🔒 ADMIN`
```http
PUT /products/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "productName": "iPhone 15 Pro",
  "productDesc": "Updated description",
  "price": 1199.99,
  "stockQuantity": 30,
  "isActive": true,
  "categoryIds": [1]
}
```

#### Delete product `🔒 ADMIN`
```http
DELETE /products/{id}
Authorization: Bearer <token>
```

---

### 🛒 Cart — `/cart`
> All endpoints require authentication

#### Get my cart
```http
GET /cart
Authorization: Bearer <token>
```

#### Add item to cart
```http
POST /cart/items?productId=1&quantity=2
Authorization: Bearer <token>
```

#### Update item quantity
```http
PUT /cart/items/{cartItemId}?quantity=5
Authorization: Bearer <token>
```

#### Remove item from cart
```http
DELETE /cart/items/{cartItemId}
Authorization: Bearer <token>
```

#### Clear cart
```http
DELETE /cart
Authorization: Bearer <token>
```

---

### 📋 Orders — `/orders`
> All endpoints require authentication

#### Checkout (place order)
```http
POST /orders/checkout
Authorization: Bearer <token>
Content-Type: application/json

{
  "shippingAddressId": 1
}
```

#### Get my orders (paginated)
```http
GET /orders?page=0&size=10
Authorization: Bearer <token>
```

#### Get order by ID
```http
GET /orders/{id}
Authorization: Bearer <token>
```

#### Cancel order
```http
PUT /orders/{id}/cancel
Authorization: Bearer <token>
```

#### Update order status `🔒 ADMIN`
```http
PUT /orders/{id}/status?status=SHIPPED
Authorization: Bearer <token>
```

> Available statuses: `PENDING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

---

### 💳 Payments — `/payments`
> All endpoints require authentication

#### Pay for an order
```http
POST /payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "orderId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

> Available methods: `CREDIT_CARD`, `DEBIT_CARD`, `PAYPAL`, `CASH_ON_DELIVERY`

#### Get payment by order
```http
GET /payments/order/{orderId}
Authorization: Bearer <token>
```

#### Refund payment
```http
PUT /payments/order/{orderId}/refund
Authorization: Bearer <token>
```

---

### 📍 Addresses — `/addresses`
> All endpoints require authentication

#### Get my addresses
```http
GET /addresses?page=0&size=10
Authorization: Bearer <token>
```

#### Save new address
```http
POST /addresses
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Abdallah Habbash",
  "street": "123 Main St",
  "city": "Nablus",
  "stateName": "West Bank",
  "postalCode": "00970",
  "country": "Palestine"
}
```

#### Delete address
```http
DELETE /addresses/{id}
Authorization: Bearer <token>
```

---

## 🔄 Typical User Flow

```
1. POST /auth/register        → Create account
2. POST /auth/login           → Get JWT token
3. GET  /products             → Browse products
4. POST /cart/items           → Add items to cart
5. POST /addresses            → Save shipping address
6. POST /orders/checkout      → Place order
7. POST /payments             → Pay for order
8. GET  /orders               → View order history
```

---

## 📁 Project Structure

```
src/main/java/com/habbashx/ecommerce/
├── config/
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthenticationController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   └── AddressController.java
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── filter/
│   └── JwtRequestFilter.java
├── repository/
├── security/
│   ├── configuration/
│   │   └── SecurityConfiguration.java
│   └── jwt/
│       └── JwtUtils.java
├── service/
└── ECommerceApplication.java
```

---

## 🛡️ Security Overview

- Passwords are hashed using **BCrypt**
- Authentication via **JWT tokens** (24h expiry by default)
- Role-based access control with `ROLE_USER` and `ROLE_ADMIN`
- Stateless sessions — no server-side session storage
- CSRF disabled (REST API)

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).