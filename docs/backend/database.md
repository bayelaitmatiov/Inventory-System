# Database Schema

The database is MySQL 8. Tables are created automatically by Spring Boot via Hibernate when the application starts (`spring.jpa.hibernate.ddl-auto=update`). You do not need to write `CREATE TABLE` statements manually.

---

## Tables Overview

| Table | Purpose |
|---|---|
| `product` | Product catalog with current stock levels |
| `warehouse` | Warehouse locations and storage capacity |
| `stock_movement` | Complete history of all stock transactions |

---

## Table: `product`

Stores all products managed by the system. The `quantity` column reflects the current stock level and is updated automatically every time a stock movement is recorded.

| Column | Java Type | MySQL Type | Description |
|---|---|---|---|
| `id` | `Long` | BIGINT PK (auto increment) | Unique product identifier |
| `name` | `String` | VARCHAR(255) | Product name |
| `description` | `String` | VARCHAR(255) | Product description |
| `price` | `Double` | DOUBLE | Unit price |
| `quantity` | `Integer` | INT | Current stock quantity |
| `category` | `String` | VARCHAR(255) | Category (e.g. Electronics, Furniture) |

**Example rows:**

| id | name | description | price | quantity | category |
|---|---|---|---|---|---|
| 1 | Laptop | 15-inch business laptop | 800.00 | 45 | Electronics |
| 2 | Office Chair | Ergonomic chair | 150.00 | 30 | Furniture |
| 3 | Notebook | A4 lined notebook | 2.50 | 200 | Stationery |

---

## Table: `warehouse`

Stores warehouse locations. Each warehouse has a maximum capacity — the system does not currently enforce this limit automatically, but it is available for future use.

| Column | Java Type | MySQL Type | Description |
|---|---|---|---|
| `id` | `Long` | BIGINT PK (auto increment) | Unique warehouse identifier |
| `name` | `String` | VARCHAR(255) | Warehouse name |
| `location` | `String` | VARCHAR(255) | Physical address or zone |
| `capacity` | `Integer` | INT | Maximum storage capacity (units) |

**Example rows:**

| id | name | location | capacity |
|---|---|---|---|
| 1 | Main Warehouse | Bishkek, Zone A | 1000 |
| 2 | Secondary Warehouse | Bishkek, Zone B | 500 |

---

## Table: `stock_movement`

Records every stock transaction in the system. Every time goods arrive (IN) or leave (OUT), a new row is inserted here. This table serves as a complete audit trail.

| Column | Java Type | MySQL Type | Description |
|---|---|---|---|
| `id` | `Long` | BIGINT PK (auto increment) | Unique movement identifier |
| `product_id` | `Long` (FK) | BIGINT → `product.id` | The product being moved |
| `warehouse_id` | `Long` (FK) | BIGINT → `warehouse.id` | The warehouse involved |
| `type` | `String` | VARCHAR(10) | Either `IN` (received) or `OUT` (dispatched) |
| `quantity` | `Integer` | INT | Number of units in this movement |
| `date` | `LocalDateTime` | DATETIME | Timestamp — set automatically when movement is created |

**Example rows:**

| id | product_id | warehouse_id | type | quantity | date |
|---|---|---|---|---|---|
| 1 | 1 | 1 | IN | 50 | 2026-04-01 09:00:00 |
| 2 | 2 | 1 | IN | 30 | 2026-04-01 09:05:00 |
| 3 | 1 | 1 | OUT | 5 | 2026-04-10 14:30:00 |

---

## Relationships

```
product ──────────────── stock_movement
                         (product_id → product.id)

warehouse ────────────── stock_movement
                         (warehouse_id → warehouse.id)
```

- One `product` can appear in many `stock_movement` rows
- One `warehouse` can appear in many `stock_movement` rows
- Each `stock_movement` belongs to exactly one product and one warehouse

These are **Many-to-One** relationships from `stock_movement`'s perspective.

In Java this is expressed with:
```java
@ManyToOne
@JoinColumn(name = "product_id")
private Product product;

@ManyToOne
@JoinColumn(name = "warehouse_id")
private Warehouse warehouse;
```

---

## Business Rules

**Stock quantity is always kept up to date:**
- When a `stock_movement` of type `IN` is saved → `product.quantity` increases by the movement's quantity
- When a `stock_movement` of type `OUT` is saved → `product.quantity` decreases by the movement's quantity
- This update happens in `StockMovementService` before saving the movement

**Stock can never go negative:**
- Before processing an OUT movement, the service checks if `product.quantity >= movement.quantity`
- If not, it throws a `RuntimeException` with message "Insufficient stock"
- The movement is not saved and the product quantity is not changed

**Low stock detection:**
- A product is considered low stock when `quantity < threshold`
- Default threshold is 10
- The dashboard fetches low stock products on load via `GET /api/products/low-stock?threshold=10`

---

## How Tables Are Created

You do not write SQL to create the tables. Spring Boot reads your `@Entity` classes and generates the tables automatically. With `ddl-auto=update`:

- If the table does not exist → it is created
- If the table exists but a column is missing → the column is added
- Existing data is never deleted

This means every time you add a new field to an entity class and restart the app, the column appears in the database automatically.