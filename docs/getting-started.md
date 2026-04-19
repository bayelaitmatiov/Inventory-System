# Getting Started

This guide walks you through setting up the Inventory & Warehouse System locally from scratch on Ubuntu.

---

## Prerequisites

You need the following installed before running the project:

- **IntelliJ IDEA** — recommended IDE for Java development
- **JDK 17** — Java Development Kit, required to compile and run the app
- **Maven 3.x** — manages dependencies and builds the project
- **MySQL 8.x** — the database the app connects to

---

## 1. Install Java 17

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Verify the installation:

```bash
java -version
# Expected output: openjdk version "17.x.x"
```

If the command is not found after installing, reload your shell:

```bash
source ~/.zshrc   # if using zsh
source ~/.bashrc  # if using bash
```

---

## 2. Install Maven

```bash
sudo apt install maven
```

Verify:

```bash
mvn -version
# Expected output: Apache Maven 3.x.x
```

Maven is used to download all project dependencies (Spring Boot, Lombok, MySQL driver, etc.) defined in `pom.xml`. You do not need to install them manually.

---

## 3. Install MySQL

```bash
sudo apt install mysql-server
sudo systemctl start mysql
```

Verify:

```bash
mysql --version
# Expected output: mysql  Ver 8.x.x
```

Run the secure installation wizard to set a root password:

```bash
sudo mysql_secure_installation
```

Follow the prompts. When asked to set a root password, choose something simple like `root` for local development.

---

## 4. Install IntelliJ IDEA

Download the Community Edition (free) from:

```
https://www.jetbrains.com/idea/download/
```

Or install via snap on Ubuntu:

```bash
sudo snap install intellij-idea-community --classic
```

---

## 5. Install the Lombok Plugin in IntelliJ

Lombok reduces boilerplate code in Java by auto-generating getters, setters, and constructors. Without the plugin, IntelliJ will show errors even though the code is correct.

1. Open IntelliJ IDEA
2. Go to `File` → `Settings` → `Plugins`
3. Search for **"Lombok"**
4. Click **Install** → Restart IntelliJ

Then enable annotation processing:

1. Go to `File` → `Settings`
2. Navigate to `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
3. Check ✅ **Enable annotation processing**
4. Click **Apply** → **OK**

---

## 6. Clone the Repository

```bash
git clone https://github.com/bayelaitmatiov/Inventory-System.git
cd Inventory-System
```

---

## 7. Create the Database

### Option A — Via terminal

```bash
sudo mysql -u root -p
```

```sql
CREATE DATABASE inventory_db;
exit;
```

### Option B — Via IntelliJ Database Tool

1. Go to `View` → `Tool Windows` → `Database`
2. Click **+** → `Data Source` → `MySQL`
3. Fill in:
    - Host: `localhost`
    - Port: `3306`
    - User: `root`
    - Password: your MySQL root password
4. Click **Test Connection** — if green, click **OK**
5. Right-click the connection → `New` → `Database` → name it `inventory_db`

The IntelliJ Database Tool lets you browse tables, view data, and run SQL queries directly inside the IDE without switching to a separate app.

---

## 8. Configure the Application

Open `src/main/resources/application.properties` and fill in your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=root
spring.datasource.password=your_password_here

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**What each property does:**

| Property | Purpose |
|---|---|
| `datasource.url` | Tells Spring where MySQL is running and which database to use |
| `datasource.username` | MySQL username |
| `datasource.password` | MySQL password |
| `ddl-auto=update` | Automatically creates/updates tables based on your entity classes |
| `show-sql=true` | Prints every SQL query to the console — useful for debugging |
| `dialect` | Tells Hibernate to generate MySQL 8 compatible SQL |

> ⚠️ `application.properties` is listed in `.gitignore` so your password is never pushed to GitHub. Share `application.properties.example` with teammates instead.

---

## 9. Install Dependencies

IntelliJ does this automatically when you open the project. If it does not, run:

```bash
mvn install
```

This downloads all dependencies listed in `pom.xml` into your local Maven cache at `~/.m2/`.

---

## 10. Run the Application

### Option A — From IntelliJ

Open `src/main/java/com/inventory/WareHouseSystemApplication.java` and click the green **▶ Run** button at the top.

### Option B — From terminal

```bash
mvn spring-boot:run
```

If everything is configured correctly you will see this in the console:

```
Tomcat started on port(s): 8080
Started WareHouseSystemApplication in 3.x seconds
```

The app is now running at `http://localhost:8080`.

---

## 11. Verify It Works

Open your browser and visit:

| URL | Page |
|---|---|
| `http://localhost:8080` | Dashboard |
| `http://localhost:8080/products.html` | Products |
| `http://localhost:8080/warehouses.html` | Warehouses |
| `http://localhost:8080/movements.html` | Stock Movements |

To test the REST API directly in the browser or Postman:

| Method | URL | Description |
|---|---|---|
| GET | `http://localhost:8080/api/products` | List all products |
| GET | `http://localhost:8080/api/warehouses` | List all warehouses |
| GET | `http://localhost:8080/api/movements` | List all movements |

---

## 12. Seed Sample Data (Optional)

To quickly populate the database with test data, open the IntelliJ Database Tool or MySQL terminal and run:

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

INSERT INTO stock_movement (product_id, warehouse_id, type, quantity, date) VALUES
(1, 1, 'IN', 50, NOW()),
(2, 1, 'IN', 30, NOW()),
(1, 1, 'OUT', 5, NOW()),
(3, 2, 'IN', 200, NOW()),
(4, 1, 'IN', 40, NOW());
```

---

## Common Issues

**`zsh: command not found: java`**
Java is not installed or not in PATH. Install it with `sudo apt install openjdk-17-jdk` then run `source ~/.zshrc`.

**Port 8080 already in use**
Another process is using port 8080. Either stop it or change the port in `application.properties`:
```properties
server.port=8081
```

**MySQL connection refused**
MySQL is not running. Start it with:
```bash
sudo systemctl start mysql
```

**Table not created automatically**
Make sure `spring.jpa.hibernate.ddl-auto=update` is set in `application.properties` and that the database `inventory_db` exists.

**Lombok errors in IntelliJ (red underlines on @Data etc.)**
The Lombok plugin is not installed or annotation processing is not enabled. Follow Step 5 above.

**Changes not reflected after editing code**
Stop the app and re-run it. Spring Boot dev tools can auto-restart if added as a dependency, but by default you need to restart manually.