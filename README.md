# Hotel Reservation System

A command-line hotel reservation application built in Java, developed as part of the Udacity Java Programming Nanodegree. The system allows customers to search for rooms, make reservations, and manage their bookings — and gives administrators tools to manage the hotel's room catalogue and view all activity.

---

## Features

**Customer**
- Create a personal account with email validation
- Search available rooms by check-in and check-out date
- Automatic fallback search (+7 days) when no rooms are available on requested dates
- Book a room and receive a confirmed reservation summary
- View all personal reservations

**Admin**
- View all registered guest accounts
- View the full room catalogue
- View all reservations across all guests
- Add new rooms (standard paid rooms or complimentary free rooms)

---

## Project Structure

```
src/
├── HotelApplication.java          # Entry point — launches main menu
├── MENU.java                      # Customer-facing CLI
├── BookingMenu.java               # Admin-facing CLI
│
├── api/
│   ├── HotelResource.java         # API layer for customer operations
│   └── AdminResource.java         # API layer for admin operations
│
├── model/
│   ├── customer/
│   │   └── Customer.java          # Guest account model
│   ├── reservation/
│   │   └── Reservation.java       # Booking record model
│   └── room/
│       ├── IRoom.java             # Room interface
│       ├── Room.java              # Standard paid room
│       ├── FreeRoom.java          # Complimentary room (extends Room)
│       └── enums/
│           └── RoomType.java      # SINGLE / DOUBLE enum
│
└── service/
    ├── customer/
    │   └── CustomerService.java   # Guest account business logic
    └── reservation/
        └── ReservationService.java # Room catalogue and booking logic
```

---

## Architecture

The application is organised into four layers. Each layer only communicates with the one directly below it — the UI never calls a service directly.

```
┌─────────────────────────────┐
│       UI  (MENU,            │  Reads user input, displays output
│           BookingMenu)      │
└────────────┬────────────────┘
             │
┌────────────▼────────────────┐
│   API  (HotelResource,      │  Translates UI calls into service calls
│         AdminResource)      │
└────────────┬────────────────┘
             │
┌────────────▼────────────────┐
│  Service  (ReservationSvc,  │  Business logic, validation, data storage
│            CustomerSvc)     │
└────────────┬────────────────┘
             │
┌────────────▼────────────────┐
│  Model  (Room, Customer,    │  Plain data objects
│          Reservation, ...)  │
└─────────────────────────────┘
```

---

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| Interface | `IRoom` — defines the contract all room types must fulfil |
| Inheritance | `FreeRoom extends Room` — complimentary room reuses paid room logic |
| Polymorphism | `IRoom` references hold both `Room` and `FreeRoom` instances |
| Encapsulation | All model fields are `private final`; accessed through public getters |
| Singleton | `ReservationService`, `CustomerService`, `HotelResource`, `AdminResource` |
| Enum | `RoomType.SINGLE` / `RoomType.DOUBLE` |
| Regex validation | `Customer` validates email format on construction |
| Exception handling | `try/catch` throughout all user input flows — no unhandled crashes |
| Switch statement | `MENU.mainMenu()` and `BookingMenu.adminMenu()` dispatch on user input |

---

## Key Implementation Details

**Double-booking prevention**  
Before recording any booking, `ReservationService.createBooking()` iterates all existing reservations and checks whether the requested room has a conflicting date window using `reservationsIntersect()`. If a conflict is found, an `IllegalArgumentException` is thrown and the booking is rejected.

**Availability algorithm**  
`getAvailableRooms()` starts with a full copy of the room catalogue as a `HashMap`, then iterates all existing bookings and removes any room with a conflicting date range. Whatever remains in the map is returned as available. This single-pass removal avoids building a separate exclusion list.

**Alternative date recommendation**  
If no rooms are found for the requested dates, `getAlternativeAvailableRooms()` runs the same availability check shifted 7 days forward. The shifted dates are shown to the customer alongside the results.

**Free rooms**  
When an admin enters a price of `$0`, the application creates a `FreeRoom` instance instead of a plain `Room`. Both implement `IRoom`, so the rest of the system handles them identically — but `FreeRoom.toString()` displays `Complimentary (Free)` instead of a price.

**Input handling**  
All user input retry loops use `while` loops — no recursion. Invalid dates, prices, room types, and emails all produce a clear error message and re-prompt without crashing.

---

## Getting Started

### Prerequisites
- Java JDK 11 or higher — download from [Adoptium](https://adoptium.net)
- VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) (or any Java IDE)

### Run in VS Code
1. Clone or download this repository
2. Open the project folder in VS Code (**File → Open Folder**)
3. Open `HotelApplication.java`
4. Click the **Run** button above the `main` method

### Run from terminal
```bash
# Compile
cd src
javac -d ../out $(find . -name "*.java")

# Run
cd ../out
java HotelApplication
```

---

## Usage Walkthrough

### 1 — Create an account
```
Select: 3
First name: Jane
Last name:  Doe
Email:      jane@example.com
→ Account created for Jane.
```

### 2 — Add rooms (Admin)
```
Select: 4 → 4
Room number: 101 | Price: $150.00 | Type: 1 (Single)
Room number: 102 | Price: $0      | Type: 2 (Double) → Complimentary room
```

### 3 — Search and book
```
Select: 1
Check-in:  06/20/2025
Check-out: 06/25/2025
→ Lists available rooms
Book? y → email → room number → Reservation confirmed
```

### 4 — Alternative date suggestion
```
Search 06/20–06/25 when all rooms are booked:
→ "No rooms for your dates. Showing availability for 06/27 → 07/02 instead."
```

### 5 — View reservations
```
Select: 2
Email: jane@example.com
→ Lists all reservations for that account
```

---

## Input Validation

| Input | Validation |
|---|---|
| Email | Regex: `^[^\s@]+@[^\s@]+\.[A-Za-z]{2,6}$` |
| Date | `SimpleDateFormat` with `setLenient(false)` — rejects invalid dates like `13/40/2025` |
| Check-out date | Must be strictly after check-in |
| Room price | Must be a number ≥ 0 |
| Room number | Cannot be blank; stored in `HashMap` so duplicates are ignored |
| Room type | Must be `1` (Single) or `2` (Double) |

---

## Dependencies

None. This project uses only the Java Standard Library (JDK 11+). No build tool or external dependencies are required.

---

## License

This project was built for educational purposes as part of the Udacity Java Programming Nanodegree.
