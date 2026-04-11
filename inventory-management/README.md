# Inventory Management System — LLD

## Problem Statement

Design an inventory management system for an e-commerce application.

**Features:**
- Add products with inventory count
- Add more inventory to existing products
- Reserve (block) inventory for a user
- Confirm a reservation
- Cancel a reservation
- Check available inventory for a product

**Constraints:** In-memory data structures, plain Java, no frameworks.

---

## Inventory Model

Two-field model on Product — not a single counter.

| Field | Meaning |
|---|---|
| `totalQuantity` | Physical stock in warehouse |
| `reserveQuantity` | Blocked by PENDING reservations |
| *derived:* `available` | `totalQuantity - reserveQuantity` |

### Why Two Fields?

A single counter conflates "physical stock" with "available to sell." With two fields, each operation has a distinct, correct impact:

```
                    totalQuantity    reserveQuantity    available (derived)
Initial:                100               0               100
After reserve(10):      100              10                90
After confirm(10):       90               0                90
After cancel(10):       100               0               100
  (if cancelled instead of confirmed)
```

- **reserve** — blocks inventory. Total stays, reserved goes up, available goes down.
- **confirm** — goods shipped. Total goes down, reserved goes down, available unchanged.
- **cancel** — block released. Total stays, reserved goes down, available goes back up.

---

## Architecture

```
inventory-management/src/main/java/
├── entity/
│   ├── Product.java              # id, name, totalQuantity, reserveQuantity, ReentrantLock
│   ├── User.java                 # id, name
│   ├── Reservation.java          # id, productId, userId, quantity, status
│   ├── ReservationStatus.java    # PENDING → CONFIRMED | CANCELLED (enum with transition logic)
│   └── IdGenerator.java          # UUID-based ID generation
├── manager/
│   ├── ProductMgr.java           # ConcurrentHashMap<String, Product>, CRUD
│   ├── ReservationMgr.java       # ConcurrentHashMap<String, Reservation>, CRUD + query
│   └── UserMgr.java              # ConcurrentHashMap<String, User>, CRUD
├── service/
│   ├── ProductService.java       # addProduct, addInventory, removeProduct, getAvailableInventory
│   └── ReservationService.java   # reserve, confirm, cancel
├── exceptions/
│   ├── ProductNotFoundException.java
│   ├── ProductNotAvailableException.java
│   ├── ProductStillReservedException.java
│   ├── ReservationNotFoundException.java
│   └── DuplicateReservationException.java
└── Main.java                     # Driver demonstrating all features
```

### Layer Responsibilities

| Layer | Responsibility | Example |
|---|---|---|
| **entity** | Data classes, enums, ID generation | `Product`, `ReservationStatus` |
| **manager** | In-memory storage (HashMap CRUD) | `ProductMgr.save()`, `findById()` |
| **service** | Business logic, validation, orchestration | `ReservationService.reserve()` |
| **exceptions** | Domain-specific error types | `ProductNotAvailableException` |

---

## Design Decisions

### 1. State Transitions in Enum

`ReservationStatus` owns its transition rules via `canTransitionTo()`:

```
PENDING  → CONFIRMED    (allowed)
PENDING  → CANCELLED    (allowed)
CONFIRMED → (terminal)
CANCELLED → (terminal)
```

Adding a new status (e.g., EXPIRED) means adding one enum value and defining its transitions. No service changes.

### 2. Row-Level Locking (ReentrantLock on Product)

Each Product carries its own `ReentrantLock`. Operations on different products run in parallel. Operations on the same product are serialized.

**Why on the entity, not in the service?**
- Both `ProductService` and `ReservationService` modify Product fields
- Lock on entity = same lock regardless of which service is calling
- Mimics database row-level locking

### 3. Idempotency — Duplicate Reservation Prevention

`reserve()` checks for an existing PENDING reservation by the same user for the same product before creating a new one. This check is inside the lock to prevent race conditions.

### 4. Entities as Data Classes

Entities hold data, services hold logic. Repositories are dumb stores. This is a deliberate architectural choice — not the only valid approach (rich domain model is the alternative).

---

## Concurrency Handling

| Threat | Protection |
|---|---|
| Two users reserve last item simultaneously | ReentrantLock on Product — availability check + mutation is atomic |
| Double confirm same reservation | State transition check inside lock — second thread sees CONFIRMED, throws |
| Double cancel same reservation | Same as above — second thread sees CANCELLED, throws |
| Confirm + cancel same reservation simultaneously | Same lock, same product — serialized |
| Duplicate reserve (user clicks twice) | Idempotency check inside lock — `findByProductIdAndUserId` |
| addInventory during reserve | Both lock the same Product — serialized |
| removeProduct while reservations exist | Checks `reserveQuantity > 0` inside lock |

### Lock Scope

```
reserve():       lock(product) → check idempotency → check availability → mutate → unlock
confirm():       lock(product) → check state transition → mutate both fields → unlock
cancel():        lock(product) → check state transition → mutate reserveQuantity → unlock
addInventory():  lock(product) → mutate totalQuantity → unlock
removeProduct():  lock(product) → check no reservations → delete → unlock
```

---

## Error Handling

| Scenario | Exception |
|---|---|
| Product not found | `ProductNotFoundException` |
| Insufficient inventory to reserve | `ProductNotAvailableException` |
| Reservation not found | `ReservationNotFoundException` |
| Invalid state transition (confirm cancelled, cancel confirmed) | `IllegalStateException` |
| Remove product with pending reservations | `ProductStillReservedException` |
| Same user reserves same product twice (PENDING exists) | `DuplicateReservationException` |

---

## Class Diagram

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│     User     │     │  ReservationStatus│     │  IdGenerator  │
├──────────────┤     │     <<enum>>      │     ├──────────────┤
│ - id         │     ├──────────────────┤     │ + generate() │
│ - name       │     │ PENDING          │     └──────────────┘
└──────────────┘     │ CONFIRMED        │
                     │ CANCELLED        │
                     ├──────────────────┤
                     │ + canTransitionTo()│
                     └──────────────────┘

┌──────────────────────┐         ┌──────────────────────────┐
│       Product         │         │       Reservation         │
├──────────────────────┤         ├──────────────────────────┤
│ - id                  │         │ - id                      │
│ - name                │◄────────│ - productId               │
│ - totalQuantity       │         │ - userId                  │
│ - reserveQuantity     │         │ - quantity                │
│ - rowLock: ReentrantLock│       │ - status: ReservationStatus│
└──────────────────────┘         └──────────────────────────┘
         ▲                                ▲
         │                                │
┌────────┴──────────┐          ┌─────────┴──────────┐
│    ProductMgr     │          │   ReservationMgr    │
│ Map<String,Product>│          │ Map<String,Reservation>│
│ save/findById/    │          │ save/findById/      │
│ delete/findAll    │          │ findByProductIdAndUserId│
└────────┬──────────┘          └─────────┬──────────┘
         │                                │
┌────────┴──────────┐          ┌─────────┴──────────┐
│  ProductService   │          │ ReservationService  │
│ addProduct        │          │ reserve             │
│ addInventory      │──uses───►│ confirm             │
│ removeProduct     │          │ cancel              │
│ getAvailableInventory│       └────────────────────┘
└───────────────────┘
```

---

## What's Not Handled (Interview Talking Points)

- **Reservation expiry** — "I'd add a createdAt timestamp and a scheduled cleanup that auto-cancels expired PENDING reservations"
- **Multiple warehouses** — "Product would need a warehouse-level inventory breakdown, not a single counter"
- **Transaction rollback** — "If product save succeeds but reservation save fails, state is inconsistent. In production I'd wrap in a transaction. Here save can't fail since it's in-memory."
- **Audit trail** — "I'd add an Observer pattern to publish events on reserve/confirm/cancel for audit logging"
