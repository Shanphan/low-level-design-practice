# Splitwise — LLD Machine Coding

Expense-sharing application. Built for Meesho SDE-3 interview practice.

## Requirements

1. Add users
2. Create groups with members
3. Add expenses with three split types: Equal, Exact, Percentage
4. Show balances for a user (who they owe, who owes them)
5. Settle balance between two users (full or partial)
6. Simplify debts (bonus — see discussion below)

## Architecture

```
Main.java (driver)
    |
    v
Service Layer
    ExpenseService    — add expense, settle balance
    GroupService      — create group, add members
    |
    v
Manager Layer (repositories)
    UserMgr           — Map<String, User>
    GroupMgr          — Map<String, Group>
    ExpenseMgr        — Map<String, Expense>
    BalanceMgr        — Map<String, Map<String, Double>> (global pairwise balances)
    |
    v
Entity Layer
    User              — id, name
    Group             — id, name, Set<memberIds>
    Expense           — id, groupId, paidByUserId, amount, splitType, splitDetails
    SplitType         — EQUAL, EXACT, PERCENTAGE (enum)
```

## Design Patterns

### Strategy Pattern — Split Calculation

```
SplitStrategy (interface)
    |--- EqualSplitStrategyImpl      — amount / participants
    |--- ExactSplitStrategyImpl      — validate sum == amount, pass through
    |--- PercentageSplitStrategyImpl — validate sum == 100%, compute amounts

SplitStrategyFactory — picks strategy from SplitType enum
```

### Factory Pattern — Strategy Selection

`SplitStrategyFactory.createStrategy(SplitType)` returns the correct strategy implementation. Adding a new split type = one new class + one case in factory. Existing code untouched (Open-Closed Principle).

## Core Flow

```
addExpense(expense, participants)
    1. Factory picks strategy from expense.splitType
    2. Strategy computes splits: Map<userId, theirShare>
    3. For each participant (skip payer):
         balanceMgr.update(payer, participant, share)
    4. Save expense

settleBalance(payerId, payeeId, amount)
    1. Validate: not self, amount > 0, debt exists, not overpaying
    2. balanceMgr.update(payee, payer, -amount)
```

## Balance Map Convention

```
balances["A"]["B"] =  300  -> B owes A 300 (A's view: positive = they owe me)
balances["B"]["A"] = -300  -> B owes A 300 (B's view: negative = I owe them)
```

Both sides updated on every operation. O(1) lookup for any user's balances.
Balances within 0.01 of zero are removed (no floating-point dust).

## Edge Cases Handled

| Edge Case | Where | Behavior |
|-----------|-------|----------|
| Zero/negative amount | ExpenseService, all strategies | IllegalArgumentException |
| Empty participants | ExpenseService, EqualSplitStrategy | IllegalArgumentException |
| Percentages != 100 | PercentageSplitStrategyImpl | InvalidPercentageSplitException |
| Exact amounts != total | ExactSplitStrategyImpl | ExactSplitAmountMismatchException |
| Null splitDetails | ExactSplit, PercentageSplit | IllegalArgumentException |
| Self-split (payer in participants) | ExpenseService loop | Skipped via continue |
| Settle with yourself | ExpenseService.settleBalance | IllegalArgumentException |
| Settle more than owed | ExpenseService.settleBalance | IllegalArgumentException |
| Settle when no debt exists | ExpenseService.settleBalance | IllegalArgumentException |
| Group not found | GroupService.addMember | GroupNotFoundException |
| Floating-point dust after settle | BalanceMgr.update | Entry removed if < 0.01 |

## Demo Scenarios (Main.java)

1. Equal split — Alice pays 900, three-way split
2. Exact split — Bob pays 1000, custom amounts
3. Percentage split — Charlie pays 600, percentage-based
4. Zero amount — throws
5. Bad percentages (sum != 100) — throws
6. Bad exact (sum != total) — throws
7. Full settle — Charlie settles 320 with Bob
8. Partial settle — Bob settles 50 with Alice
9. Self-settle — throws
10. Overpay settle — throws

## Simplify Debts — Discussion Note

Not implemented. This is a bonus/extension question. Here's how to discuss it in the interview:

**The Problem:**
```
A owes B 500
B owes C 500
C owes A 500
= 3 transactions

Simplified:
Everyone nets to 0. Zero transactions needed.
```

**The Approach:**
1. Compute net balance for each user (sum of all they owe minus all owed to them)
2. Separate into creditors (positive net) and debtors (negative net)
3. Greedily match largest creditor with largest debtor
4. This is the min-cash-flow problem — solvable with a greedy algorithm or modeled as a directed graph

**Data Structure:** The existing `balanceSheet` has all pairwise data needed. Compute net per user:
```java
Map<String, Double> netBalances = new HashMap<>();
for (String user : allUsers) {
    double net = balanceSheet.get(user).values().stream()
        .mapToDouble(Double::doubleValue).sum();
    netBalances.put(user, net);
}
```

Then use two heaps (max-heap for creditors, min-heap for debtors) to greedily match and minimize transactions.

**What to say:** "I'd model this as a min-cash-flow problem. Compute net balances, use a greedy approach with heaps to match creditors and debtors. This minimizes the number of transactions. For a distributed system, I'd run this as a batch job since it reads the full balance graph."

## Concurrency (Follow-up)

If interviewer asks "make it thread-safe":
1. Swap HashMap to ConcurrentHashMap in all managers
2. Add ReentrantLock per user-pair in BalanceMgr.update() (or synchronized)
3. AtomicLong already used in IdGenerator
4. Per-entity locking — different user-pair operations run in parallel

## Project Structure

```
src/main/java/
    Main.java
    entity/
        User.java
        Group.java
        Expense.java
        SplitType.java
    manager/
        UserMgr.java
        GroupMgr.java
        ExpenseMgr.java
        BalanceMgr.java
    Service/
        ExpenseService.java
        GroupService.java
        IdGenerator.java
    strategy/
        SplitStrategy.java (interface)
        EqualSplitStrategyImpl.java
        ExactSplitStrategyImpl.java
        PercentageSplitStrategyImpl.java
        SplitStrategyFactory.java
    exception/
        ExactSplitAmountMismatchException.java
        InvalidPercentageSplitException.java
        GroupNotFoundException.java
```
