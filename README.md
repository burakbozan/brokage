# Brokage Firm Challenge

### 1. Project Setup:

**Spring Boot:** We'll use Spring Boot for rapid application development.

**H2 Database:** An in-memory database for development and testing.

**Maven:** For dependency management and building.

**Spring Data JPA:** For database interaction.

**Spring Security:** For authentication and authorization.

**Lombok:** For reducing boilerplate code (optional, but recommended).

**JUnit and Mockito:** For unit testing.

### 2. Entities:

Asset, Order, Customer, Side, Status 

### 3. Repositories:

AssetRepository, OrderRepository, CustomerRepository

### 4. Services:

OrderService(createOrder, listOrders, deleteOrder, listAssets, matchOrder)

### 5. Controllers:

OrderController(createOrder, listOrders, deleteOrder, listAssets, matchOrder)

### 6. Security Configuration (Spring Security):

Implement authentication and authorization using Spring Security.

### 7. Unit Tests:

Write unit tests for the service layer using JUnit and Mockito.

### 8.Build and Run:

Use Maven or Gradle to build the project (mvn spring-boot:run)
Access the API endpoints using tools like Postman or curl.

### 9. Bonus Features:

    Bonus 1: Add a Customer entity and implement customer-specific authorization.

Bonus 2: Implement the matchOrder endpoint for admin users.

Add security configuration.