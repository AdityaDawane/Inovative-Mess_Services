# 🍽️ Mess Management System

A full-stack **campus mess management web application** built with **Spring Boot** and **Vanilla JS**, designed to digitize and streamline cafeteria operations for students, vendors, and administrators.

---

## 🚀 Live Demo

> _Add your deployment link here (e.g., Render, Railway, Vercel)_

---

## 📌 Overview

The Mess Management System replaces traditional paper-based mess operations with a digital platform featuring **QR-code-based meal attendance**, **subscription management**, **payment tracking**, and **role-based dashboards** for three user types — Students, Vendors, and Admins.

---

## ✨ Features

### 👨‍🎓 Student (Customer) Portal
- Register and log in securely with JWT authentication
- Browse available meal plans from vendors
- Subscribe to meal plans and pay online (Razorpay integration)
- View a personal **QR code** for daily meal check-in
- Track attendance history and remaining meals
- View active subscriptions, payment status, and renewal dates

### 🏪 Vendor Portal
- Secure vendor login and profile management
- Create and manage **meal plans** (daily, weekly, monthly)
- Publish **daily menus** (Breakfast / Lunch / Dinner)
- **Scan student QR codes** to mark attendance
- Approve or reject subscription requests from students
- View pending payments and student rosters
- Generate **attendance and revenue reports**

### 🛡️ Admin Portal
- Centralized admin dashboard
- Manage all students and vendors
- Monitor subscriptions and payments across the platform
- Full control over meal plans and system settings

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 4.x |
| **Security** | Spring Security, JWT (jjwt 0.11.5) |
| **ORM** | Spring Data JPA, Hibernate |
| **Database** | MySQL |
| **Payment** | Razorpay API |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript |
| **Build Tool** | Apache Maven |
| **Other** | Lombok, ModelMapper, BCrypt |

---

## 🏗️ Architecture

```
mess-management-system/
├── Frontend/                       # Static HTML/CSS/JS frontend
│   ├── index.html                  # Landing page
│   ├── student-dashboard.html      # Student portal
│   ├── vendor-dashboard.html       # Vendor portal
│   ├── vendor-scanner.html         # QR code scanner
│   ├── vendor-reports.html         # Attendance & revenue reports
│   └── ...
│
└── src/main/java/com/campusmenu/
    ├── controller/                 # REST API Controllers
    │   ├── AdminController.java
    │   ├── CustomerController.java
    │   ├── VendorController.java
    │   ├── MenuController.java
    │   └── PaymentController.java
    ├── entity/                     # JPA Entities
    │   ├── Customer, Vendor, Admin
    │   ├── MealPlan, Subscription
    │   ├── Attendance, Payment, DailyMenu
    ├── service/                    # Business Logic Layer
    ├── repository/                 # Spring Data JPA Repositories
    ├── dto/                        # Request & Response DTOs
    ├── security/                   # JWT Auth Filter & Config
    └── exception/                  # Global Exception Handler
```

---

## 🔐 API Endpoints

### Authentication
| Method | Endpoint | Role |
|---|---|---|
| POST | `/api/customer/register` | Public |
| POST | `/api/customer/login` | Public |
| POST | `/api/vendor/login` | Public |
| POST | `/api/admin/login` | Public |

### Customer
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/customer/profile/{id}` | Get profile |
| GET | `/api/customer/subscriptions/{id}` | View subscriptions |
| POST | `/api/customer/subscribe` | Subscribe to a meal plan |
| GET | `/api/customer/qr/{subscriptionId}` | Get QR code |
| GET | `/api/customer/attendance/{id}` | View attendance history |

### Vendor
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/vendor/menu` | Add daily menu |
| GET | `/api/vendor/students` | View subscribed students |
| POST | `/api/vendor/scan-qr` | Scan student QR code |
| GET | `/api/vendor/attendance` | View attendance records |
| GET | `/api/vendor/reports` | Revenue & attendance reports |
| PUT | `/api/vendor/approve/{subscriptionId}` | Approve subscription |

### Admin
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/students` | All students |
| GET | `/api/admin/vendors` | All vendors |
| POST | `/api/admin/meal-plans` | Create meal plan |
| GET | `/api/admin/payments` | All payments |

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.8+
- Node.js (optional, for serving frontend)

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/mess-management-system.git
cd mess-management-system
```

### 2. Set Up the Database
```bash
mysql -u root -p
CREATE DATABASE mess_management;
USE mess_management;
SOURCE database.sql;
```

### 3. Configure Application Properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mess_management
spring.datasource.username=your_username
spring.datasource.password=your_password

app.jwt.secret=your_jwt_secret_key
app.jwt.expiration=86400000

razorpay.key.id=your_razorpay_key
razorpay.key.secret=your_razorpay_secret
```

### 4. Build and Run the Backend
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8081`

### 5. Open the Frontend
Open `Frontend/index.html` in your browser, or serve it with:
```bash
cd Frontend
npx serve .
```

---

## 🗄️ Database Schema

Key tables:
- `customers` — Student accounts
- `vendors` — Mess vendor accounts
- `admins` — Admin accounts
- `meal_plans` — Available subscription plans with pricing and meal inclusions
- `subscriptions` — Student-to-vendor meal subscriptions with QR codes
- `attendance` — QR scan records (unique per student per meal per day)
- `payments` — Payment transactions with Razorpay order tracking
- `daily_menus` — Daily menu items per vendor per meal type

---

## 📸 Screenshots

> _Add screenshots of your dashboards here_

---

## 🔮 Future Improvements

- Push notifications for meal reminders
- Mobile app (Android/iOS)
- Analytics dashboard with charts
- Multi-language support
- Automated subscription renewal

---

## 👨‍💻 Author

**Your Name**  
📧 your.email@example.com  
🔗 [LinkedIn](https://linkedin.com/in/yourprofile) | [GitHub](https://github.com/your-username)

---

## 📄 License

This project is licensed under the MIT License.
