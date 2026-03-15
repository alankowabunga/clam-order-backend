# AGENTS.md - Clam Order Backend Developer Guide

## Project Overview

Spring Boot 3.3.0 / Java 17 backend for 蛤蠣訂單系統 (Clam Order System). Clean Architecture with 4 layers:
- **domain** - Core business logic, entities, value objects
- **application** - Use cases, DTOs, mappers
- **infrastructure** - Config, security, persistence
- **presentation** - REST controllers, exception handling

---

## Build & Test Commands

### Common Commands
```bash
# Compile
./gradlew compileJava

# Run all tests
./gradlew test

# Build JAR (skip tests)
./gradlew build -x test

# Run application
./gradlew bootRun
```

### Running Single Test
```bash
# Run specific test class
./gradlew test --tests DiscountPolicyTest

# Run specific test method
./gradlew test --tests "DiscountPolicyTest.testBulkDiscount_Tier1_10Jin"

# Run tests matching pattern
./gradlew test --tests "*DiscountTest"
```

### Other
```bash
# Clean build
./gradlew clean build

# View test report
open build/reports/tests/test/index.html
```

---

## Code Style Guidelines

### Architecture Patterns

**Clean Architecture Layers:**
- Domain: Entities, Value Objects, Repository interfaces, Domain Services
- Application: Use Cases, DTOs, Mappers
- Infrastructure: Config classes, Security, Persistence
- Presentation: Controllers, Exception Handlers

**Key Principles:**
- Domain layer has NO external dependencies (no Spring annotations)
- Use cases orchestrate domain objects
- Controllers are thin - delegate to use cases
- Value objects are immutable

### Java Conventions

**Package Structure:**
```
com.project.clamorderbackend/
├── domain/
│   ├── entity/          # JPA entities
│   ├── valueobject/    # Immutable value objects
│   ├── service/        # Domain services
│   └── repository/     # Repository interfaces
├── application/
│   ├── dto/            # Data transfer objects
│   ├── usecase/       # Application services
│   └── mapper/         # DTO mappers
├── infrastructure/
│   ├── config/        # Spring configuration
│   └── security/      # Security config
└── presentation/
    ├── controller/    # REST endpoints
    └── advice/       # Exception handlers
```

**Naming:**
- Classes: `PascalCase` (e.g., `OrderController`, `DiscountPolicy`)
- Methods: `camelCase` (e.g., `calculateBulkDiscount`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `BULK_DISCOUNT_TIER_1_THRESHOLD`)
- Database columns: `snake_case` (e.g., `price_per_catty`)

### Imports

**Order imports by category (no blank lines between):**
1. Java/Jakarta EE (`java.math`, `jakarta.persistence`)
2. Spring (`org.springframework.*`)
3. Lombok (`lombok.*`)
4. Project internal (`com.project.clamorderbackend.*`)

### Lombok Usage

Use Lombok to reduce boilerplate:
- `@Entity`, `@Table` for JPA entities
- `@Getter`, `@Setter` for getters/setters
- `@NoArgsConstructor`, `@AllArgsConstructor` for constructors
- `@Builder` for builder pattern
- `@RequiredArgsConstructor` for constructor injection in controllers/services

```java
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;
}
```

### DTOs

**Request DTOs:**
- Use `@Valid` for validation in controllers
- Use meaningful field names matching API contract
- Add validation annotations (`@NotNull`, `@NotBlank`, `@Size`, etc.)

```java
public record OrderCalculateRequest(
    @NotNull(message = "items 不能為空")
    List<OrderItemRequest> items,
    
    @NotNull(message = "deliveryMethod 不能為空")
    String deliveryMethod,
    
    String district
) {}
```

**Response DTOs:**
- Use `@JsonInclude(JsonInclude.Include.NON_NULL)` to exclude nulls
- Use Java records for simple DTOs (Java 17+)

### Controller Guidelines

- Use `@RestController` and `@RequestMapping`
- Use `@RequiredArgsConstructor` for dependency injection
- Use `@Valid` on request bodies
- Return `ResponseEntity<T>` for flexibility
- Add Javadoc comments for endpoints

```java
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    /**
     * Calculate order price
     */
    @PostMapping("/calculate")
    public ResponseEntity<OrderCalculateResponse> calculatePrice(
            @Valid @RequestBody OrderCalculateRequest request) {
        return ResponseEntity.ok(orderUseCase.calculatePrice(request));
    }
}
```

### Exception Handling

Use `GlobalExceptionHandler` for centralized exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        // Return formatted validation errors
    }
}
```

### Testing

**Test Location:** `src/test/java/` - mirror main package structure

**Test Naming:** `<ClassName>Test.java` (e.g., `DiscountPolicyTest.java`)

**Test Method Naming:**
- `test<Method>_<Scenario>_<Expected>` (e.g., `testBulkDiscount_Tier1_10Jin`)
- Use Chinese comments to document scenarios

```java
@Test
void testBulkDiscount_Tier1_10Jin() {
    // 滿10斤，每斤-5元
    BigDecimal discount = DiscountPolicy.calculateBulkDiscount(10);
    assertEquals(BigDecimal.valueOf(-50), discount);
}
```

**Test Structure:**
- Group tests by category with comments: `// ==================== Bulk Discount Tests ====================`
- Use AAA pattern: Arrange, Act, Assert
- Use static imports for assertions: `import static org.junit.jupiter.api.Assertions.*;`

### Database

- Use PostgreSQL
- JPA entities map to snake_case columns via `@Column`
- Use `@PrePersist` / `@PreUpdate` for timestamps

### Configuration

- `application.yaml` for all config (no `.properties`)
- Actuator health endpoint at `/actuator/health`

### Git Conventions

- Commit message format: `[type] description` (e.g., `[feat] add order calculation`)
- Types: `feat`, `fix`, `refactor`, `test`, `docs`
- Create feature branches from `main`

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/products` | List products |
| POST | `/api/v1/order/calculate` | Calculate order price |
| POST | `/api/v1/order/submit` | Submit order |
| GET | `/api/v1/admin/orders/export` | Export orders (CSV) |

**Swagger UI:** http://localhost:8080/swagger-ui/index.html

---

## Dependencies

- Spring Boot 3.3.0
- Spring Data JPA
- Spring Security
- H2 (dev) / PostgreSQL (prod)
- Lombok
- SpringDoc OpenAPI 2.5.0

---

## Common Issues

1. **H2 Console Access:** Already enabled at `/h2-console` (dev only)
2. **Port 8080 Conflict:** Check `application.yaml` server.port
3. **Test Failures:** Ensure H2 is available (JUnit 5 with JUnit Platform)
