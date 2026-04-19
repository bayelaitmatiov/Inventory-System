# Frontend UI Specs
Just a test for git
The frontend consists of plain HTML + CSS pages served directly by Spring Boot. JavaScript `fetch()` calls are used to interact with the REST API. No frameworks, no build step.

---

## Pages

### `index.html` — Dashboard
**URL:** `http://localhost:8080`

The main landing page. Gives a quick overview of the system.

**Contents:**
- Navigation links to all other pages
- Total product count (fetches from `GET /api/products`)
- Total warehouse count (fetches from `GET /api/warehouses`)
- Low stock alert list (fetches from `GET /api/products/low-stock?threshold=10`) — shows products that are running low so the manager can act quickly

---

### `products.html` — Product Management
**URL:** `http://localhost:8080/products.html`

Manage the product catalog.

**Contents:**
- Form at the top to add a new product (name, description, price, quantity, category)
- Table below listing all products with columns: ID, Name, Category, Price, Quantity
- Each row has **Edit** and **Delete** buttons
- Edit opens the form pre-filled with that product's data
- Delete removes the product after confirmation

**API calls used:**
- `GET /api/products` — load table
- `POST /api/products` — add product
- `PUT /api/products/{id}` — update product
- `DELETE /api/products/{id}` — delete product

---

### `warehouses.html` — Warehouse Management
**URL:** `http://localhost:8080/warehouses.html`

Same structure as the products page but for warehouses.

**Contents:**
- Form to add/edit a warehouse (name, location, capacity)
- Table listing all warehouses
- Edit and Delete buttons per row

**API calls used:**
- `GET /api/warehouses`
- `POST /api/warehouses`
- `PUT /api/warehouses/{id}`
- `DELETE /api/warehouses/{id}`

---

### `movements.html` — Stock Movements
**URL:** `http://localhost:8080/movements.html`

Record stock coming in or going out, and view the full movement history.

**Contents:**
- Form to record a movement:
    - Select product (dropdown)
    - Select warehouse (dropdown)
    - Select type: IN or OUT (dropdown)
    - Enter quantity (number input)
    - Submit button
- Table below showing all past movements: Date, Product, Warehouse, Type, Quantity
- If a stock OUT would bring quantity below 0, an error message is shown

**API calls used:**
- `GET /api/products` — populate product dropdown
- `GET /api/warehouses` — populate warehouse dropdown
- `GET /api/movements` — load history table
- `POST /api/movements` — submit new movement

---

## UI Design Rules

- **White background**, clean and minimal
- **Table borders** on all cells for readability
- **Simple button styles** — distinct colors for Edit (blue) and Delete (red)
- **No CSS frameworks** — all styling is plain CSS
- **No CDN links** — everything runs offline
- Forms are above their related tables on the same page
- Navigation bar at the top of every page with links to all pages

---

## How Frontend Talks to Backend

Each page uses the browser's built-in `fetch()` API to call the REST endpoints:

```javascript
// Example: load all products
fetch('/api/products')
  .then(res => res.json())
  .then(products => {
    // render products into the table
  });
```

Since the HTML files are served by the same Spring Boot server on port 8080, no CORS issues occur. The `@CrossOrigin` annotation on controllers is there as a safety measure for any external tools like Postman.