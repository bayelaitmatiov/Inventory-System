# Getting Started

This guide walks you through setting up the Inventory & Warehouse System locally from scratch.

---
### Prerequisites

Make sure you have the following installed:

- **IntelliJ IDEA** (recommended IDE)
- **JDK 17** — Java Development Kit
- **Maven 3.x** — dependency and build manager
- **MySQL 8.x** — database

### Verify installations

```bash
java -version     # should say 17
mvn -version      # should say 3.x.x
mysql --version   # should say 8.x.x
```

---

## 1. Clone the Repository

```bash
git clone https://github.com/bayelaitmatiov/Inventory-System.git
cd Inventory-System
```

---

## 2. Create the Database

Open MySQL and run:

```sql
CREATE DATABASE inventory_db;
```

You can do this via:
- **IntelliJ Database Tool** — `View` → `Tool Windows` → `Database`
- **MySQL terminal** — `mysql -u root -p`

---

## 3. Configure the Application

Open `src/main/resources/application.properties` and set your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

> ⚠️ Do not commit `application.properties` with your real password to GitHub.
> Keep an `application.properties.example` with placeholder values instead.

---

## 4. Install Dependencies

IntelliJ does this automatically when you open the project. If not, run:

```bash
mvn install
```

---

## 5. Run the Application

Open `src/main/java/com/inventory/WareHouseSystemApplication.java` and click the green **▶ Run** button in IntelliJ.

Or via terminal:

```bash
mvn spring-boot:run
```

If everything is correct you will see:

```
Tomcat started on port(s): 8080
Started WareHouseSystemApplication
```

The app is now running at `http://localhost:8080`.

---

## 6. Verify It Works

Open your browser and go to:

| URL | What you see |
|---|---|
| `http://localhost:8080` | Dashboard (index.html) |
| `http://localhost:8080/products.html` | Products page |
| `http://localhost:8080/warehouses.html` | Warehouses page |
| `http://localhost:8080/movements.html` | Stock movements page |

To test the REST API directly:

| URL | Description |
|---|---|
| `GET http://localhost:8080/api/products` | List all products |
| `GET http://localhost:8080/api/warehouses` | List all warehouses |
| `GET http://localhost:8080/api/movements` | List all stock movements |

---

## 7. Seed Sample Data (Optional)

To quickly populate the database with test data, run this SQL:

```sql
USE inventory_db;

INSERT INTO warehouse (name, location, capacity) VALUES
('Main Warehouse', 'Bishkek, Zone A', 1000),
('Secondary Warehouse', 'Bishkek, Zone B', 500);

INSERT INTO product (name, description, price, quantity, category) VALUES
('Laptop', '15-inch business laptop', 800.00, 50, 'Electronics'),
('Office Chair', 'Ergonomic chair', 150.00, 30, 'Furniture'),
('Notebook', 'A4 lined notebook', 2.50, 200, 'Stationery'),
('Monitor', '24-inch Full HD', 250.00, 40, 'Electronics'),
('Desk', 'Wooden office desk', 300.00, 15, 'Furniture');
```

---

## Common Issues

**Port 8080 already in use**
Another app is using the port. Either stop it or change the port in `application.properties`:
```properties
server.port=8081
```

**MySQL connection refused**
Make sure MySQL is running:
```bash
sudo systemctl start mysql   # Linux
```

**Table not created automatically**
Check that `spring.jpa.hibernate.ddl-auto=update` is set in `application.properties`.

**Lombok not working**
Go to `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → enable **annotation processing**.