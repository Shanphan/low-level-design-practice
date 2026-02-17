# BookMyShow - Low Level Design

## Overview
A movie ticket booking system that allows users to search movies, browse shows across theatres, select seats, and book tickets with thread-safe concurrency handling.

## Features
- **searchMovies()** — Find movies by city
- **getShowsForMovie()** — Get all shows for a movie across theatres in a city
- **getAvailableSeats()** — View available seats for a selected show
- **bookSeats()** — Thread-safe seat reservation with payment

## Design Patterns & Principles
| Pattern | Where | Why |
|---|---|---|
| Singleton (double-checked locking) | MovieManager, TheatreManager, BookingManager | Single shared instance across the app |
| Manager Pattern | All managers | Centralized business logic per entity |
| Service Layer | BokMyShow | Orchestrates the booking workflow |
| Composition over Inheritance | Show has Movie + Screen; Booking has Show + Seats | Flexible entity relationships |
| ReentrantLock + Consistent Lock Ordering | TheatreManager.reserveSeats() | Thread-safe booking, deadlock prevention |

## Project Structure
```
book-my-show/src/main/java/
├── entity/
│   ├── Movie.java          — id, name, duration, genre
│   ├── Theatre.java        — id, city, address, screens, shows
│   ├── Screen.java         — id, name, capacity, seatLayout, screenType
│   ├── Show.java           — showId, movie, screen, showTime, seatAvailability
│   ├── Seat.java           — id, row, seatNumber, seatType, price, ReentrantLock
│   ├── Booking.java        — id, show, bookedSeats, totalPrice, status, payment
│   └── Payment.java        — id, paymentMode, amount
├── enums/
│   ├── BookingStatus.java  — PENDING, CONFIRMED, CANCELLED, FAILED
│   ├── PaymentMode.java    — UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING
│   ├── ScreenType.java     — TWO_D, THREE_D, FOUR_D, IMAX
│   └── SeatType.java       — REGULAR, PREMIUM, VIP
├── manager/
│   ├── MovieManager.java   — Manages movies by city (add, search)
│   ├── TheatreManager.java — Manages theatres, shows, seat reservation
│   └── BookingManager.java — Creates bookings with payment
└── service/
    └── BokMyShow.java      — Orchestrator + simulation entry point
```

## Class Diagram
```
                    ┌─────────────┐
                    │  BokMyShow  │ (Service)
                    └──────┬──────┘
            ┌──────────────┼──────────────┐
            ▼              ▼              ▼
    ┌──────────────┐ ┌──────────────┐ ┌───────────────┐
    │ MovieManager │ │TheatreManager│ │BookingManager  │
    └──────┬───────┘ └──────┬───────┘ └──────┬────────┘
           │                │                │
           ▼                ▼                ▼
      ┌─────────┐    ┌──────────┐     ┌──────────┐
      │  Movie  │    │ Theatre  │     │ Booking  │
      └─────────┘    └────┬─────┘     └────┬─────┘
                          │ 1..*           │
                ┌─────────┴────────┐       │ 1
                ▼                  ▼       ▼
          ┌──────────┐       ┌─────────┐ ┌──────────┐
          │  Screen  │       │  Show   │ │ Payment  │
          └────┬─────┘       └────┬────┘ └──────────┘
               │ 1..*            │
               ▼                 │
          ┌──────────┐           │
          │   Seat   │◄──────────┘ 1..*
          └──────────┘

    Show ──────► Movie     (1)
    Show ──────► Screen    (1)  — copies seatLayout into seatAvailability
    Booking ──► Show       (1)
    Booking ──► Seat       (1..*)
    Booking ──► Payment    (1)
```

## Booking Flow
```
1. searchMovies(city)
       │
       ▼
2. getShowsForMovie(city, movieName)
       │
       ▼
3. getAvailableSeats(show)
       │
       ▼
4. bookSeats(show, seats, paymentMode)
       │
       ├── Lock seats (sorted by ID to prevent deadlocks)
       ├── Validate seats belong to show
       ├── Check availability
       ├── Mark seats as booked
       ├── Create Payment
       └── Create Booking (status: CONFIRMED)
```

## Concurrency Handling
- Each `Seat` has a `ReentrantLock`
- Seats are sorted by ID before locking to prevent deadlocks
- All locks are released in a `finally` block to guarantee cleanup
