# MakeMyTrip - Low Level Design

## Overview
A flight booking system that allows users to search flights, view available seats on an airplane, book tickets, and cancel bookings with thread-safe concurrency handling.

## Features
- **searchFlights()** — Find flights by source city, destination city, and date
- **getAvailableSeats()** — View available seats on a selected flight
- **bookFlight()** — Thread-safe seat reservation with payment
- **cancelBooking()** — Release seats back to available pool

## Design Patterns & Principles
| Pattern | Where | Why |
|---|---|---|
| Singleton (double-checked locking) | AirportManager, FlightManager, BookingManager | Single shared instance across the app |
| Manager Pattern | All managers | Centralized business logic per entity |
| Service Layer | MakeMyTrip | Orchestrates the booking workflow |
| Composition over Inheritance | Flight has Airplane + Airports; Booking has Flight + Passenger | Flexible entity relationships |
| ReentrantLock + Consistent Lock Ordering | FlightManager.reserveSeats() | Thread-safe booking, deadlock prevention |
| ID-based Resolution | reserveSeats(flight, seatIds) | Resolves seats from flight map — avoids detached object mutation |

## Project Structure
```
make-my-trip/src/main/java/
├── entity/
│   ├── Airport.java        — id, name, code (IATA), city
│   ├── Airplane.java       — id, model, airline, capacity, seatLayout
│   ├── Flight.java         — id, flightNumber, airplane, source, destination, schedule, seatAvailability
│   ├── Seat.java           — id, seatNumber, seatType, price, seatStatus, ReentrantLock
│   ├── Passenger.java      — id, name, age, email
│   ├── Booking.java        — id, flight, passenger, bookedSeats, totalPrice, status, payment
│   └── Payment.java        — id, paymentMode, amount
├── enums/
│   ├── FlightStatus.java   — SCHEDULED, DELAYED, CANCELLED, COMPLETED
│   ├── SeatType.java       — ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST_CLASS
│   ├── SeatStatus.java     — AVAILABLE, BOOKED, BLOCKED
│   ├── BookingStatus.java  — PENDING, CONFIRMED, CANCELLED, FAILED
│   └── PaymentMode.java    — UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING
├── manager/
│   ├── AirportManager.java — Manages airports by code and city
│   ├── FlightManager.java  — Search flights, reserve/release seats
│   └── BookingManager.java — Create and cancel bookings with payment
└── service/
    └── MakeMyTrip.java     — Orchestrator + simulation entry point
```

## Class Diagram
```
                    ┌─────────────┐
                    │ MakeMyTrip  │ (Service)
                    └──────┬──────┘
            ┌──────────────┼──────────────┐
            ▼              ▼              ▼
    ┌────────────────┐ ┌──────────────┐ ┌───────────────┐
    │ AirportManager │ │FlightManager │ │BookingManager  │
    └──────┬─────────┘ └──────┬───────┘ └──────┬────────┘
           │                  │                │
           ▼                  ▼                ▼
      ┌──────────┐     ┌──────────┐     ┌──────────┐
      │ Airport  │     │  Flight  │     │ Booking  │
      └──────────┘     └────┬─────┘     └────┬─────┘
           ▲                │                │
           │ 2              │ 1              │ 1
      (src + dest)          ▼                ▼
                      ┌──────────┐     ┌───────────┐
                      │ Airplane │     │ Passenger  │
                      └────┬─────┘     └───────────┘
                           │ 1..*
                           ▼
                      ┌──────────┐     ┌──────────┐
                      │   Seat   │     │ Payment  │
                      └──────────┘     └──────────┘

    Flight ────► Airplane    (1)  — copies seatLayout into seatAvailability
    Flight ────► Airport     (2: source + destination)
    Airplane ──► Seat        (1..*)
    Booking ──► Flight       (1)
    Booking ──► Passenger    (1)
    Booking ──► Seat         (1..*)
    Booking ──► Payment      (1)
```

## Booking Flow
```
1. searchFlights(sourceCity, destCity, date)
       │
       ▼
2. getAvailableSeats(flight)
       │
       ▼
3. bookFlight(flight, passenger, seatIds, paymentMode)
       │
       ├── Resolve seat IDs to actual Seat objects from flight map
       ├── Lock seats (sorted by ID to prevent deadlocks)
       ├── Check availability
       ├── Mark seats as BOOKED
       ├── Create Payment
       └── Create Booking (status: CONFIRMED)

4. cancelBooking(bookingId)
       │
       ├── Lock seats (sorted by ID)
       ├── Mark seats as AVAILABLE
       └── Update Booking (status: CANCELLED)
```

## Concurrency Handling
- Each `Seat` has a `ReentrantLock`
- Seats are sorted by ID before locking to prevent deadlocks
- All locks are released in a `finally` block to guarantee cleanup
- `reserveSeats()` takes seat IDs (not objects) and resolves actual references from the flight's seat map — prevents detached object mutation bugs

## Key Design Decision: Airplane → Flight Seat Copy
Similar to how `Screen` owns the seat layout and `Show` copies it in BookMyShow:
- **Airplane** defines the physical seat layout
- **Flight** copies seats from its Airplane on creation
- Each flight gets independent seat availability — same airplane can serve multiple flights
