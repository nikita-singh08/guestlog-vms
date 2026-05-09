# GuestLog - Visitor Management System

GuestLog is a Java Swing based desktop application used to manage college visitors digitally.  
The project helps in storing visitor details, managing records, and improving visitor tracking.

---

## Features

- Visitor Registration Form
- Modern Java Swing UI
- Admin Dashboard
- Super Admin Dashboard
- MySQL Database Integration
- Store Visitor Records
- Search Visitor
- Seat Information Display
- Department and Course Selection

---

# Tech Stack

| Technology | Usage |
|------------|-------|
| Java | Core Development |
| Java Swing | GUI Design |
| MySQL | Database |
| JDBC | Database Connectivity |
| Eclipse IDE | Development Environment |

## Project Structure

```text
src/
│
├── com.guestlog.dao
├── com.guestlog.db
├── com.guestlog.main
├── com.guestlog.model
├── com.guestlog.service
├── com.guestlog.ui.admin
├── com.guestlog.ui.superadmin
├── com.guestlog.ui.visitor
└── com.guestlog.utils
```

---

## Project Flow

```text
Start Application
        ↓
Role Selection Screen
        ↓
 ┌───────────────┬───────────────┬────────────────┐
 │               │               │
Visitor        Admin        Super Admin
 │               │               │
Visitor      Dashboard      Dashboard
Registration     │               │
Form             │               │
 │          Manage Records   View Analytics
 │          Search Visitor   Manage Admins
 │          Seat Management  Export Reports
 │               │               │
 └───────────────┴───────────────┘
                 ↓
          MySQL Database
```

# Modules Implemented

## Visitor Module

- Visitor Registration Form
- Visitor Data Storage
- Department & Course Selection

## Admin Module

- Admin Login
- Dashboard UI
- Visitor Management

## Super Admin Module

- Super Admin Authentication
- Dashboard Management

# Database

The project uses MySQL database for storing:

- Visitor Records
- Admin Information
- Super Admin Credentials

---

# Future Enhancements

- QR Code Entry System
- Export Visitor Reports
- Email Notifications
- Visitor Photo Capture
- Analytics Dashboard
- Web-Based Version

## How to Run

### 1. Clone Repository

```bash
git clone https://github.com/nikita-singh08/guestlog-vms.git
```

### 2. Open Project
Import the project into Eclipse IDE.

### 3. Configure Database
Create MySQL database and update JDBC credentials.

Database Name:
```text
guestlog
```

### 4. Add JDBC Driver
Add MySQL Connector JAR into project libraries.

### 5. Run Application
Run the main Java file:
```text
ModernVisitorRegistration.java
```

---

## Project Repository

[GuestLog GitHub Repository](https://github.com/nikita-singh08/guestlog-vms)




