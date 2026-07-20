# JK1 - Enterprise eCommerce Application (Phase 1)

This project contains the architectural foundation and boilerplate for the JK1 eCommerce application, following a Clean Architecture and Package-by-Layer approach.

## Technologies Used
*   Java 21 (LTS)
*   Spring Boot 3.5.x
*   Maven
*   MySQL 8 (config ready)
*   Thymeleaf
*   Bootstrap 5.3 & Bootstrap Icons
*   Spring Data JPA & Hibernate
*   Lombok

## Architecture & Structure
The project leverages the standard Spring Boot MVC structure:
*   `config` - Configuration classes
*   `constants` - Application constants
*   `controller` - MVC controllers
*   `service` / `service.impl` - Business logic interfaces and implementations
*   `repository` - Data access interfaces
*   `entity` - JPA entities
*   `dto` / `dto.request` / `dto.response` - Data Transfer Objects
*   `mapper` - Object mappers
*   `validation` / `validation.annotations` / `validation.validator` - Custom validation constraints
*   `security` - Security configuration
*   `exception` - Global exception handling
*   `helper` - Helper classes
*   `util` - Utility classes

## Running the Application
Ensure you have Java 21 installed.
```bash
./mvnw spring-boot:run
```
*(Optionally modify the active profile in `application.properties` and the database configuration in `application-dev.properties` to connect to a real MySQL instance. When you do, make sure to remove `spring.autoconfigure.exclude` from `application.properties`)*
