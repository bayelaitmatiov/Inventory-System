# API Documentation

**Base URL:** `http://localhost:8080/api`

All responses are JSON. No authentication is required — this is an internal management system. You can test all endpoints using a browser (GET only) or Postman (all methods).

---

## Products

### `GET /api/products`

Returns a list of all products in the system.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "15-inch business laptop",
    "price": 800.00,
    "quantity": 45,
    "category": "Electronics"
  },
  {
    "id": 2,
    "name": "Office Chair",
    "description": "Ergonomic chair",
    "price": 150.00,
    "quantity": 30,
    "category": "Furniture"
  }
]
```

---

### `GET /api/products/{id}`

Returns a single product by its ID.

**Example:** `GET /api/products/1`

**Response `200 OK`:**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "15-inch business laptop",
  "price": 800.00,
  "quantity": 45,
  "category": "Electronics"
}
```

**Response `404 Not Found`:**
```json
{
  "message": "Product not found with id: 1"
}
```

---

### `GET /api/products/low-stock?threshold=10`

Returns all products whose quantity is below the given threshold. Used by the dashboard to show low stock alerts.

**Query param:** `threshold` — integer, default is 10

**Example:** `GET /api/products/low-stock?threshold=10`

**Response `200 OK`:**
```json
[
  {
    "id": 4,
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 60.00,
    "quantity": 3,
    "category": "Electronics"
  }
]
```

Returns an empty array `[]` if no products are low on stock.

---

### `POST /api/products`

Creates a new product.

**Request body:**
```json
{
  "name": "Monitor",
  "description": "24-inch Full HD",
  "price": 250.00,
  "quantity": 40,
  "category": "Electronics"
}
```

**Response `201 Created`:**
```json
{
  "id": 5,
  "name": "Monitor",
  "description": "24-inch Full HD",
  "price": 250.00,
  "quantity": 40,
  "category": "Electronics"
}
```

---

### `PUT /api/products/{id}`

Updates an existing product. All fields must be included in the request.

**Example:** `PUT /api/products/1`

**Request body:**
```json
{
  "name": "Laptop Pro",
  "description": "Updated 15-inch business laptop",
  "price": 950.00,
  "quantity": 45,
  "category": "Electronics"
}
```

**Response `200 OK`:** Returns the updated product object.

**Response `404 Not Found`:** If the product ID does not exist.

---

### `DELETE /api/products/{id}`

Permanently deletes a product by ID.

**Example:** `DELETE /api/products/1`

**Response `204 No Content`:** Product deleted successfully, no body returned.

**Response `404 Not Found`:** If the product ID does not exist.

---

## Warehouses

### `GET /api/warehouses`

Returns all warehouses.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "name": "Main Warehouse",
    "location": "Bishkek, Zone A",
    "capacity": 1000
  },
  {
    "id": 2,
    "name": "Secondary Warehouse",
    "location": "Bishkek, Zone B",
    "capacity": 500
  }
]
```

---

### `GET /api/warehouses/{id}`

Returns a single warehouse by ID.

**Response `200 OK`:**
```json
{
  "id": 1,
  "name": "Main Warehouse",
  "location": "Bishkek, Zone A",
  "capacity": 1000
}
```

**Response `404 Not Found`:** If the warehouse ID does not exist.

---

### `POST /api/warehouses`

Creates a new warehouse.

**Request body:**
```json
{
  "name": "North Storage",
  "location": "Bishkek, Zone C",
  "capacity": 750
}
```

**Response `201 Created`:** Returns the created warehouse with its generated ID.

---

### `PUT /api/warehouses/{id}`

Updates an existing warehouse.

**Request body:** Same shape as POST.

**Response `200 OK`:** Returns the updated warehouse object.

---

### `DELETE /api/warehouses/{id}`

Deletes a warehouse by ID.

**Response `204 No Content`:** Deleted successfully.

---

## Stock Movements

### `GET /api/movements`

Returns the full stock movement history, ordered by date.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "product": {
      "id": 1,
      "name": "Laptop",
      "description": "15-inch business laptop",
      "price": 800.00,
      "quantity": 45,
      "category": "Electronics"
    },
    "warehouse": {
      "id": 1,
      "name": "Main Warehouse",
      "location": "Bishkek, Zone A",
      "capacity": 1000
    },
    "type": "IN",
    "quantity": 50,
    "date": "2026-04-01T09:00:00"
  }
]
```

---

### `GET /api/movements/product/{productId}`

Returns all stock movements for a specific product. Useful for tracking a single product's history.

**Example:** `GET /api/movements/product/1`

**Response `200 OK`:** Array of movement objects filtered by product.

---

### `POST /api/movements`

Records a new stock movement (IN or OUT). This also automatically updates the product's quantity.

**Request body:**
```json
{
  "product": { "id": 1 },
  "warehouse": { "id": 1 },
  "type": "OUT",
  "quantity": 5
}
```

**Response `201 Created`:**
```json
{
  "id": 3,
  "product": { "id": 1, "name": "Laptop", ... },
  "warehouse": { "id": 1, "name": "Main Warehouse", ... },
  "type": "OUT",
  "quantity": 5,
  "date": "2026-04-19T14:30:00"
}
```

**Response `400 Bad Request`** (if stock is insufficient):
```json
{
  "message": "Insufficient stock"
}
```

The date is set automatically by the server — you do not need to include it in the request.

---

## HTTP Status Codes

| Code | Meaning | When it happens |
|---|---|---|
| `200 OK` | Success | GET and PUT requests that succeed |
| `201 Created` | Resource created | POST requests that succeed |
| `204 No Content` | Success, no body | DELETE requests that succeed |
| `400 Bad Request` | Invalid request | e.g. trying to take out more stock than available |
| `404 Not Found` | Resource missing | ID does not exist in the database |
| `500 Internal Server Error` | Server crashed | Unexpected error — check the IntelliJ console |

---

## Testing with Postman

Postman is a tool for sending HTTP requests to test your API without needing the frontend.

1. Download Postman from `https://www.postman.com/downloads/`
2. Create a new request
3. Set the method (GET, POST, PUT, DELETE)
4. Enter the URL (e.g. `http://localhost:8080/api/products`)
5. For POST and PUT, go to **Body** → select **raw** → select **JSON** → paste your request body
6. Click **Send**