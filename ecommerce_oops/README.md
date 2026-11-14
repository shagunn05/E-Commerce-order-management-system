# 🛒 E-Commerce Order Management System (Core Java OOP Project)

A 100% self-contained, dependency-free Java project built with **6 core Java OOP files**. It requires no build tools (no Maven or Gradle required) and compiles instantly with standard `javac` and runs with `java`.

---

## 📁 The 6 Core Project Files

| File Name | Primary Concepts Demonstrated | Description |
| :--- | :--- | :--- |
| **1. [Product.java](file:///f:/Management%20system/ecommerce_oops/Product.java)** | **Abstraction & Encapsulation** | Abstract base class, `Discountable` interface contract, defensive getters/setters, custom exceptions (`InsufficientStockException`, `InvalidProductException`), and `Comparable<Product>` sorting. |
| **2. [ElectronicsProduct.java](file:///f:/Management%20system/ecommerce_oops/ElectronicsProduct.java)** | **Inheritance & Polymorphism** | Subclass extending `Product`. Overrides `calculateTax()` (18% electronic tax), `calculateDiscount()` (10% bulk discount for 2+ units), and `displayDetails()`. |
| **3. [ClothingProduct.java](file:///f:/Management%20system/ecommerce_oops/ClothingProduct.java)** | **Inheritance & Polymorphism** | Subclass extending `Product`. Overrides `calculateTax()` (5% textile tax), `calculateDiscount()` (15% bundle discount for 3+ units), and `displayDetails()`. |
| **4. [Customer.java](file:///f:/Management%20system/ecommerce_oops/Customer.java)** | **Encapsulation & Business Rules** | Customer profile tracking membership tiers (`Regular`, `Silver`, `Gold`, `Platinum`) and automated tier upgrades via loyalty points. |
| **5. [Order.java](file:///f:/Management%20system/ecommerce_oops/Order.java)** | **Collections & Aggregation** | Manages order items with Java Collections (`List<Product>`, `Map<Product, Integer>`, `Set<String>`), throws custom `InsufficientStockException`, and calculates subtotal, discounts, and taxes dynamically. |
| **6. [Main.java](file:///f:/Management%20system/ecommerce_oops/Main.java)** | **Entry Point & Exception Demo** | Executable class with `public static void main`. Features an **Automated Test Suite** (sorting, polymorphism, and live try-catch exception triggers) + an **Interactive Console Menu**. |

---

## ⚡ How to Run

### Method 1: Double-Click Batch File (Windows)
Double-click [`run_ecommerce.bat`](file:///f:/Management%20system/run_ecommerce.bat) in your root folder.

### Method 2: Command Line (PowerShell or Command Prompt)
```cmd
cd "f:\Management system\ecommerce_oops"
javac -d bin Product.java ElectronicsProduct.java ClothingProduct.java Customer.java Order.java Main.java
java -cp bin Main
```
