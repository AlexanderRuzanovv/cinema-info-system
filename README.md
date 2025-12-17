# Cinema Store Information System

Spring Boot 3 web app for managing a cinema store: products, categories, suppliers, orders, and role-based access.

## Features
- User registration and login (Spring Security)
- Role model: **Customer, Seller, Manager, Admin**
- CRUD for products, categories, suppliers
- Order management with statuses
- Search, filtering, sorting, pagination
- Admin panel for user management
- UI: Thymeleaf + Bootstrap

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Security, Spring Data JPA (Hibernate)
- Databases: PostgreSQL (prod)
- Thymeleaf, Bootstrap 5
- Maven

## Quick Start (H2)
```bash
mvn clean package
mvn spring-boot:run
# App: http://localhost:8080
```

## PostgreSQL Setup
```sql
CREATE DATABASE filmstore;
```
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/filmstore
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```
Then run:
```bash
mvn spring-boot:run
```

## Default Accounts
| Login    | Password | Role        |
|----------|----------|-------------|
| admin    | admin    | ADMIN       |
| manager  | manager  | MANAGER     |
| seller   | seller   | SELLER      |
| customer | customer | CUSTOMER    |

## Key Endpoints
- Public: `GET /`, `/login`, `/register`, `POST /register`
- Authenticated: `GET /dashboard`, `/products`, `/categories`, `/profile`
- Customer: `GET /orders/my`, `POST /orders/new`
- Seller/Manager: `GET /orders`, `POST /orders/{id}/status`
- Manager: `GET /suppliers`, `POST /products/new`, `POST /categories/new`
- Admin: `GET /admin`, `GET /admin/users`, `POST /admin/users/{id}/role`

## Project Structure (short)
```
src/main/java/com/cinemastore
├─ config/            # Security config, data initializer
├─ controller/        # MVC controllers
├─ entity/            # JPA entities
├─ repository/        # Spring Data repositories
├─ service/           # Business logic
└─ security/          # Custom user details
src/main/resources
├─ application.properties
└─ templates/         # Thymeleaf views
```
