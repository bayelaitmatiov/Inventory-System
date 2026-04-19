# Frontend UI Specs

The frontend consists of plain HTML, CSS, and vanilla JavaScript files served directly by Spring Boot. No frameworks, no build tools, no CDN links required. Everything runs from the same server on port 8080.

---

## How the Frontend Works

When the Spring Boot app starts, it automatically serves any files placed in `src/main/resources/static/` at the root URL. So `static/index.html` is available at `http://localhost:8080/`, and `static/products.html` is available at `http://localhost:8080/products.html`.

JavaScript on each page uses the browser's built-in `fetch()` API to call the REST endpoints. Since the HTML and the API are on the same server and port, no CORS configuration is needed on the frontend side.

**Basic fetch pattern used on every page:**
```javascript
// Load data and render into table
async function loadProducts() {
  const response = await fetch('/api/products');
  const products = await response.json();

  const tbody = document.getElementById('product-table-body');
  tbody.innerHTML = '';

  products.forEach(product => {
    const row = `<tr>
      <td>${product.id}</td>
      <td>${product.name}</td>
      <td>${product.category}</td>
      <td>${product.price}</td>
      <td>${product.quantity}</td>
      <td>
        <button onclick="editProduct(${product.id})">Edit</button>
        <button onclick="deleteProduct(${product.id})">Delete</button>
      </td>
    </tr>`;
    tbody.innerHTML += row;
  });
}
```

---

## Pages

---

### `index.html` — Dashboard
**URL:** `http://localhost:8080`

The main landing page. Gives a quick overview of the system state at a glance.

**Layout:**
- Navigation bar at the top with links to all pages
- Three summary cards:
  - Total number of products (fetches `GET /api/products`, counts the array)
  - Total number of warehouses (fetches `GET /api/warehouses`, counts the array)
  - Number of low stock products (fetches `GET /api/products/low-stock?threshold=10`, counts the array)
- Low stock alert table below the cards showing: Product Name, Category, Current Quantity
- If no products are low on stock, a green message is shown instead: "All products are sufficiently stocked"

**API calls:**
```
GET /api/products                          → count total products
GET /api/warehouses                        → count total warehouses
GET /api/products/low-stock?threshold=10   → show low stock alert table
```

---

### `products.html` — Product Management
**URL:** `http://localhost:8080/products.html`

Manage the full product catalog. Add new products, update existing ones, and remove products that are no longer needed.

**Layout:**
- Navigation bar at the top
- A form above the table with fields:
  - Name (text input)
  - Description (text input)
  - Price (number input)
  - Quantity (number input)
  - Category (text input)
  - Submit button — labeled "Add Product" by default, changes to "Update Product" when editing
  - Cancel button — appears only during edit mode, resets the form
- Hidden input field storing the current product ID during edit mode
- Table below the form listing all products with columns: ID, Name, Category, Price, Quantity, Actions
- Each row has two action buttons:
  - **Edit** (blue) — pre-fills the form with the product's data for updating
  - **Delete** (red) — removes the product after a browser confirm dialog

**Edit flow:**
1. User clicks Edit on a row
2. Form fields are populated with that product's data
3. Submit button label changes to "Update Product"
4. User modifies fields and submits
5. A `PUT /api/products/{id}` request is sent
6. Table reloads with updated data
7. Form resets to "Add" mode

**API calls:**
```
GET    /api/products        → load and render table
POST   /api/products        → submit add form
PUT    /api/products/{id}   → submit edit form
DELETE /api/products/{id}   → delete button
```

---

### `warehouses.html` — Warehouse Management
**URL:** `http://localhost:8080/warehouses.html`

Same structure and behavior as the products page but for warehouses.

**Form fields:**
- Name (text input)
- Location (text input)
- Capacity (number input)

**Table columns:** ID, Name, Location, Capacity, Actions

**API calls:**
```
GET    /api/warehouses        → load table
POST   /api/warehouses        → add warehouse
PUT    /api/warehouses/{id}   → update warehouse
DELETE /api/warehouses/{id}   → delete warehouse
```

---

### `movements.html` — Stock Movements
**URL:** `http://localhost:8080/movements.html`

Record stock arriving at or leaving a warehouse, and view the complete movement history.

**Layout:**
- Navigation bar at the top
- Form at the top with fields:
  - Product (dropdown — populated from `GET /api/products`)
  - Warehouse (dropdown — populated from `GET /api/warehouses`)
  - Type (dropdown — options: IN, OUT)
  - Quantity (number input)
  - Submit button labeled "Record Movement"
- Error message area — shown in red if the server rejects the movement (e.g. insufficient stock)
- Success message area — shown in green on successful submission
- History table below showing all past movements with columns: Date, Product, Warehouse, Type, Quantity
- Table is sorted by date descending (most recent first)

**Behavior on submit:**
1. A `POST /api/movements` request is sent
2. If successful → success message shown, history table reloads, form resets
3. If the server returns 400 (insufficient stock) → red error message shown: "Insufficient stock for this product"
4. The product's quantity in the database is updated automatically by the backend

**API calls:**
```
GET  /api/products    → populate product dropdown
GET  /api/warehouses  → populate warehouse dropdown
GET  /api/movements   → load history table
POST /api/movements   → submit form
```

---

## Navigation Bar

Every page has the same navigation bar at the top:

```
[ Dashboard ]  [ Products ]  [ Warehouses ]  [ Stock Movements ]
```

The current page's link is visually highlighted (bold or underlined) so the user always knows where they are.

---

## CSS Design Rules

- **Background:** white (`#ffffff`)
- **Font:** system default sans-serif
- **Tables:** full-width, with solid borders on all cells (`border: 1px solid #ccc`), compact padding
- **Form inputs:** full-width within their container, with a visible border
- **Buttons:**
  - Edit → blue background (`#007bff`), white text
  - Delete → red background (`#dc3545`), white text
  - Submit → green background (`#28a745`), white text
  - Cancel → grey background (`#6c757d`), white text
- **Cards (dashboard):** light grey background, rounded corners, centered number in large bold font
- **Error messages:** red text, shown inline below the form
- **Success messages:** green text, shown inline below the form
- No external CSS libraries or CDN links — all styles written in `<style>` tags or a local `styles.css` file

---

## File Organization in `static/`

```
src/main/resources/static/
├── index.html
├── products.html
├── warehouses.html
├── movements.html
└── styles.css       # shared styles used by all pages (optional)
```

If `styles.css` is used, each HTML page links to it with:
```html
<link rel="stylesheet" href="/styles.css">
```