# Gym Chain Management System

Machine coding problem — actually asked at Meesho SDE-3 interviews.

## Problem

Design and implement an online portal for a gym chain managing multiple gyms across a city. Admins manage gyms and classes. Customers book, cancel, and view bookings. All in-memory, plain Java.

## Architecture

```
Main.java (driver + demo)
│
├── service/
│   ├── GymService          — admin ops: add/remove gym, remove class (with booking checks)
│   ├── GymClassService     — class CRUD + 6AM-8PM time validation
│   ├── BookingService      — book, cancel, view bookings, availability
│   └── CustomerService     — customer CRUD
│
├── repository/
│   ├── GymRepository       — HashMap<String, Gym>
│   ├── GymClassRepository  — ConcurrentHashMap<String, GymClass>
│   ├── BookingRepository   — ConcurrentHashMap<String, Booking>
│   └── CustomerRepository  — HashMap<String, Customer>
│
├── entity/
│   ├── Gym, GymClass, Booking, Customer
│   ├── BookingResponse     — response DTO for book/cancel
│   ├── BookingStatus       — CONFIRMED, CANCELLED
│   ├── ClassType           — YOGA, PILATES, ZOMBA
│   └── EntityIdGenerator   — AtomicLong per prefix, thread-safe
│
└── exception/              — (placeholder for custom exceptions)
```

## Key Design Decisions

- **No Admin entity** — requirements don't track which admin did what
- **Booking status: CONFIRMED / CANCELLED** — no PENDING state, concurrency solved by locks not states
- **Capacity from bookings** — count confirmed bookings, don't maintain a separate counter
- **One booking per customer per class per day** — duplicate check filters on CONFIRMED status so cancel-then-rebook works
- **GymService as admin hub** — owns removeGym and removeClass with active booking validation, avoids circular dependency
- **EntityIdGenerator** — ConcurrentHashMap of AtomicLong per prefix (GYM-1, CLASS-1, BOOKING-1)

## Edge Cases Handled

- Class full → rejected
- Double booking same class same day → rejected
- Cancel already cancelled booking → rejected
- Remove gym/class with active bookings → rejected
- Class outside 6AM-8PM → rejected
- Rebook after cancel (freed seat) → allowed

## TODO

- [ ] Add concurrency: ReentrantLock per classId on bookClass
- [ ] Swap remaining HashMap repos to ConcurrentHashMap
- [ ] Validate gym exists in addClass
- [ ] Run and verify all Main.java demo scenarios
