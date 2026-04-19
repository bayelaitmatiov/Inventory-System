# Backend Folder Architecture

The backend is a Spring Boot application following a standard layered architecture. Each layer has a single responsibility and only communicates with the layer directly below it.

---

## Full Project Structure

```
WareHouseSystem/
├── src/
│   ├── main/
│   │   ├── java/com/inventory/
│   │   │   ├── controller/        # REST API endpoints
│   │   │   ├── service/           # Business logic
│   │   │   ├── repository/        # Database access
│   │   │   ├── model/             # JPA entity classes (database tables)
│   │   │   └── WareHouseSystemApplication.java   # App entry point
│   │   └── resources/
│   │       ├── static/            # Frontend files (HTML, CSS, JS)
│   │       └── application.properties  # Database and server config
├── pom.xml                        # Maven dependencies
└── .gitignore
```

---

## Layer Breakdown

---

### `model/` — Entities

JPA entity classes that map directly to MySQL tables. Spring Boot reads these classes and automatically creates the corresponding tables in the database on startup (when `ddl-auto=update` is set).

Each entity uses these annotations:
- `@Entity` — marks the class as a database table
- `@Id` + `@GeneratedValue` — marks the primary key and makes it auto-increment
- `@ManyToOne` + `@JoinColumn` — defines a foreign key relationship to another table
- `@Column` — optional, used to customize column names or constraints
- Lombok `@Data` — auto-generates getters, setters, equals, hashCode, toString
- Lombok `@NoArgsConstructor` + `@AllArgsConstructor` — auto-generates constructors

**Example — Product entity:**
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String category;
}
```

| Class | Table | Description |
|---|---|---|
| `Product` | `product` | Product catalog with stock levels |
| `Warehouse` | `warehouse` | Warehouse locations and capacity |
| `StockMovement` | `stock_movement` | Full history of stock in/out transactions |

---

### `repository/` — Data Access

Spring Data JPA repository interfaces. You declare the interface and Spring automatically provides the implementation — no SQL required for basic operations.

Every repository extends `JpaRepository<Entity, IdType>` which gives you these methods for free:

| Method | SQL equivalent |
|---|---|
| `findAll()` | `SELECT * FROM table` |
| `findById(id)` | `SELECT * WHERE id = ?` |
| `save(entity)` | `INSERT` or `UPDATE` |
| `deleteById(id)` | `DELETE WHERE id = ?` |

On top of the built-in methods, you can define custom queries just by following Spring's naming convention:

**Example — ProductRepository:**
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // SELECT * FROM product WHERE category = ?
    List<Product> findByCategory(String category);

    // SELECT * FROM product WHERE quantity < ?
    List<Product> findByQuantityLessThan(int threshold);
}
```

Spring reads the method name and generates the correct SQL automatically. No implementation needed.

| Interface | Custom Methods |
|---|---|
| `ProductRepository` | `findByCategory`, `findByQuantityLessThan` |
| `WarehouseRepository` | none (basic CRUD is enough) |
| `StockMovementRepository` | `findByProductId`, `findByType` |

---

### `service/` — Business Logic

Service classes contain the actual rules of the application. Controllers call services; services call repositories. This keeps controllers thin and makes the business logic easy to test and modify independently.

Each service class is annotated with `@Service` so Spring manages it as a bean and injects it where needed.

**Example — StockMovementService (core business rule):**
```java
@Service
public class StockMovementService {

    public StockMovement addMovement(StockMovement movement) {
        Product product = productRepository.findById(movement.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (movement.getType().equals("IN")) {
            product.setQuantity(product.getQuantity() + movement.getQuantity());
        } else if (movement.getType().equals("OUT")) {
            if (product.getQuantity() < movement.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }
            product.setQuantity(product.getQuantity() - movement.getQuantity());
        }

        productRepository.save(product);
        movement.setDate(LocalDateTime.now());
        return stockMovementRepository.save(movement);
    }
}
```

| Class | Key Responsibilities |
|---|---|
| `ProductService` | CRUD for products, return low stock list |
| `WarehouseService` | CRUD for warehouses |
| `StockMovementService` | Record movements, update product quantity, validate stock never goes below 0 |

---

### `controller/` — REST Endpoints

Controllers receive HTTP requests from the browser, call the appropriate service method, and return a JSON response. They contain no business logic — just routing.

Each controller uses:
- `@RestController` — marks as a REST API controller, responses are automatically serialized to JSON
- `@RequestMapping("/api/...")` — sets the base URL for all endpoints in the class
- `@CrossOrigin(origins = "*")` — allows the HTML frontend (served on the same port) to call the API
- `ResponseEntity<?>` — wraps the response with the correct HTTP status code

**Example — ProductController:**
```java
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(productService.createProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```

| Class | Base Path | Endpoints |
|---|---|---|
| `ProductController` | `/api/products` | GET all, GET by id, GET low-stock, POST, PUT, DELETE |
| `WarehouseController` | `/api/warehouses` | GET all, GET by id, POST, PUT, DELETE |
| `StockMovementController` | `/api/movements` | GET all, GET by product, POST |

---

### `resources/static/` — Frontend

Static HTML, CSS, and JavaScript files placed here are automatically served by Spring Boot's embedded Tomcat server. No separate web server needed.

| File | Purpose |
|---|---|
| `index.html` | Dashboard — total counts and low stock alerts |
| `products.html` | Product list with add/edit/delete form |
| `warehouses.html` | Warehouse list with add/edit/delete form |
| `movements.html` | Stock movement form and history table |

These files use the browser's `fetch()` API to call the REST endpoints on the same server.

---

## Request Flow

```
Browser (HTML page)
        ↓  fetch('/api/products')
ProductController  (@RestController)
        ↓  productService.getAllProducts()
ProductService  (@Service)
        ↓  productRepository.findAll()
ProductRepository  (JpaRepository)
        ↓  SELECT * FROM product
MySQL Database
        ↑  rows returned
ProductRepository
        ↑  List<Product>
ProductService
        ↑  List<Product>
ProductController
        ↑  JSON array response
Browser
```

---

## `pom.xml` — Dependencies

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | Enables REST API and embeds a Tomcat server so you don't need to deploy to a separate server |
| `spring-boot-starter-data-jpa` | Enables JPA and Hibernate ORM for database access |
| `mysql-connector-j` | JDBC driver that lets Java talk to MySQL |
| `lombok` | Reduces boilerplate — generates getters, setters, and constructors at compile time |

---

## Why This Architecture?

Each layer only knows about the layer below it:

- Controller knows about Service
- Service knows about Repository
- Repository knows about the Database

This means you can change the database (e.g. switch from MySQL to PostgreSQL) without touching the controllers. Or you can change business rules in the service without touching the API endpoints. Each layer is independent and replaceable.