# API Documentation

**Base URL:** `http://localhost:8080/api`

All responses are in JSON format. No authentication required (this is an internal system).

---

## Products

### `GET /api/products`
Returns a list of all products.

**Response:**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "15-inch business laptop",
    "price": 800.00,
    "quantity": 50,
    "category": "Electronics"
  }
]
```

---

### `GET /api/products/{id}`
Returns a single product by ID.

**Response:** Single product object. Returns `404` if not found.

---

### `GET /api/products/low-stock?threshold=10`
Returns all products whose quantity is below the given threshold.

**Query param:** `threshold` — integer (default: 10)

**Response:** Array of product objects with low stock.

---

### `POST /api/products`
Create a new product.

**Request:**
```json
{
  "name": "Monitor",
  "description": "24-inch Full HD",
  "price": 250.00,
  "quantity": 40,
  "category": "Electronics"
}
```

**Response:** Created product object with generated `id`. Returns `201 Created`.

---

### `PUT /api/products/{id}`
Update an existing product.

**Request:** Same shape as POST. All fields required.

**Response:** Updated product object. Returns `404` if not found.

---

### `DELETE /api/products/{id}`
Delete a product by ID.

**Response:** `204 No Content`. Returns `404` if not found.

---

## Warehouses

### `GET /api/warehouses`
Returns a list of all warehouses.

**Response:**
```json
[
  {
    "id": 1,
    "name": "Main Warehouse",
    "location": "Bishkek, Zone A",
    "capacity": 1000
  }
]
```

---

### `GET /api/warehouses/{id}`
Returns a single warehouse by ID.

---

### `POST /api/warehouses`
Create a new warehouse.

**Request:**
```json
{
  "name": "Secondary Warehouse",
  "location": "Bishkek, Zone B",
  "capacity": 500
}
```

**Response:** Created warehouse object. Returns `201 Created`.

---

### `PUT /api/warehouses/{id}`
Update an existing warehouse.

---

### `DELETE /api/warehouses/{id}`
Delete a warehouse by ID.

---

## Stock Movements

### `GET /api/movements`
Returns all stock movements (full history).

**Response:**
```json
[
  {
    "id": 1,
    "product": { "id": 1, "name": "Laptop", ... },
    "warehouse": { "id": 1, "name": "Main Warehouse", ... },
    "type": "IN",
    "quantity": 20,
    "date": "2026-04-19T10:30:00"
  }
]
```

---

### `GET /api/movements/product/{productId}`
Returns all stock movements for a specific product.

---

### `POST /api/movements`
Record a new stock movement (IN or OUT).

**Request:**
```json
{
  "product": { "id": 1 },
  "warehouse": { "id": 1 },
  "type": "OUT",
  "quantity": 5
}
```

**Response:** Created movement object. Returns `400 Bad Request` if stock would go below 0.

---

## Error Responses

| Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid input (e.g. insufficient stock) |
| `404 Not Found` | Resource does not exist |
| `500 Internal Server Error` | Unexpected server error |

All errors return a message in the response body explaining what went wrong.