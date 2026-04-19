# Database Schema

The database is MySQL. Tables are auto-created by Spring Boot via `spring.jpa.hibernate.ddl-auto=update` — no manual SQL needed to create them.

---

## Tables Overview

| Table | Purpose |
|---|---|
| `product` | Product catalog with stock levels |
| `warehouse` | Warehouse locations and capacity |
| `stock_movement` | History of all stock in/out transactions |

---

## Table: `product`

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT PK (auto) | Unique product identifier |
| `name` | VARCHAR | Product name |
| `description` | VARCHAR | Product description |
| `price` | DOUBLE | Unit price |
| `quantity` | INT | Current stock quantity |
| `category` | VARCHAR | Product category (e.g. Electronics, Furniture) |

---

## Table: `warehouse`

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT PK (auto) | Unique warehouse identifier |
| `name` | VARCHAR | Warehouse name |
| `location` | VARCHAR | Physical location |
| `capacity` | INT | Maximum storage capacity |

---

## Table: `stock_movement`

Records every stock transaction — both incoming and outgoing.

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT PK (auto) | Unique movement identifier |
| `product_id` | BIGINT FK → `product.id` | The product being moved |
| `warehouse_id` | BIGINT FK → `warehouse.id` | The warehouse involved |
| `type` | VARCHAR | Either `IN` (received) or `OUT` (dispatched) |
| `quantity` | INT | Number of units moved |
| `date` | DATETIME | Timestamp of the movement (auto set) |

---

## Relationships

```
product ──────────── stock_movement (product_id → product.id)
warehouse ─────────── stock_movement (warehouse_id → warehouse.id)
```

- One product can have many stock movements
- One warehouse can have many stock movements
- Each stock movement belongs to exactly one product and one warehouse

---

## Business Rules

- When a `stock_movement` of type `IN` is created → `product.quantity` increases
- When a `stock_movement` of type `OUT` is created → `product.quantity` decreases
- `product.quantity` can never go below 0 — the system throws an error if attempted
- A product is considered **low stock** when its `quantity` falls below a defined threshold (default: 10)