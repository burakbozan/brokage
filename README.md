# Brokage Firm Challenge

### 1. Project Setup:

**Spring Boot:** We'll use Spring Boot for rapid application development.

**H2 Database:** An in-memory database for development and testing.

**Maven:** For dependency management and building.

**Spring Data JPA:** For database interaction.

**Spring Security:** For authentication and authorization.

**JUnit and Mockito:** For unit testing.

### 2. Entities:

Asset, Order, Customer, Side, Status 

### 3. Repositories:

AssetRepository, OrderRepository, CustomerRepository

### 4. Services:

OrderService, CustomerService

### 5. Controllers:

OrderController, AuthController

### 6. Security Configuration (Spring Security):

Implement authentication and authorization using Spring Security with Basic Auth.

### 7. Unit Tests:

Write unit tests for the service layer using JUnit and Mockito.

### 8.Build and Run:

Use Maven to build the project (mvn spring-boot:run)
Access the API endpoints using tools like Postman or curl.

#### Workflow:

Register a new user: Send a POST request to /auth/register with a username and password in the request body.

Authenticate as the new user: For subsequent requests to /api/customer/** endpoints, configure HTTP Basic Authentication with the username and password you just registered.

Get your assets: Send a GET request to /api/customer/assets.

Place a buy order: Send a POST request to /api/customer/orders with the assetName, side=BUY, size, and price as query parameters.

List your orders: Send a GET request to /api/customer/orders with startDate and endDate parameters.

### 9. Bonus Features:

Bonus 1: Add a Customer entity and implement customer-specific authorization.

Bonus 2: Implement the matchOrder endpoint for admin users.

Add security configuration.
