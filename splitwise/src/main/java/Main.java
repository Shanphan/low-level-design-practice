import Service.ExpenseService;
import Service.GroupService;
import entity.Expense;
import entity.Group;
import entity.SplitType;
import entity.User;
import manager.BalanceMgr;
import manager.ExpenseMgr;
import manager.GroupMgr;
import manager.UserMgr;

import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        // --- Wire dependencies ---
        UserMgr userMgr = new UserMgr();
        GroupMgr groupMgr = new GroupMgr();
        ExpenseMgr expenseMgr = new ExpenseMgr();
        BalanceMgr balanceMgr = new BalanceMgr();

        GroupService groupService = new GroupService(groupMgr);
        ExpenseService expenseService = new ExpenseService(expenseMgr, groupMgr, balanceMgr);

        // --- Create users ---
        User alice = userMgr.save(new User("Alice"));
        User bob = userMgr.save(new User("Bob"));
        User charlie = userMgr.save(new User("Charlie"));

        System.out.println("=== Users Created ===");
        System.out.println("Alice: " + alice.getId());
        System.out.println("Bob: " + bob.getId());
        System.out.println("Charlie: " + charlie.getId());

        // --- Create group ---
        Group trip = groupService.addGroup(new Group("Goa Trip"));
        groupService.addMember(trip.getId(), alice.getId());
        groupService.addMember(trip.getId(), bob.getId());
        groupService.addMember(trip.getId(), charlie.getId());

        System.out.println("\n=== Group Created ===");
        System.out.println("Group: " + trip.getId() + " Members: " + trip.getMemberId());

        // --- Scenario 1: Equal Split ---
        // Alice pays 900 dinner, split equally among all three
        System.out.println("\n=== Scenario 1: Equal Split ===");
        System.out.println("Alice pays 900 for dinner, split equally among Alice, Bob, Charlie");

        Expense dinner = new Expense(trip.getId(), alice.getId(), 900.0, SplitType.EQUAL);
        Set<String> allThree = Set.of(alice.getId(), bob.getId(), charlie.getId());
        expenseService.addExpense(dinner, allThree);

        printBalances(balanceMgr, "Alice", alice.getId());
        printBalances(balanceMgr, "Bob", bob.getId());
        printBalances(balanceMgr, "Charlie", charlie.getId());

        // --- Scenario 2: Exact Split ---
        // Bob pays 1000 hotel, Alice owes 200, Bob owes 300, Charlie owes 500
        System.out.println("\n=== Scenario 2: Exact Split ===");
        System.out.println("Bob pays 1000 for hotel. Alice: 200, Bob: 300, Charlie: 500");

        Expense hotel = new Expense(trip.getId(), bob.getId(), 1000.0, SplitType.EXACT);
        hotel.setSplitDetails(Map.of(
                alice.getId(), 200.0,
                bob.getId(), 300.0,
                charlie.getId(), 500.0
        ));
        expenseService.addExpense(hotel, allThree);

        printBalances(balanceMgr, "Alice", alice.getId());
        printBalances(balanceMgr, "Bob", bob.getId());
        printBalances(balanceMgr, "Charlie", charlie.getId());

        // --- Scenario 3: Percentage Split ---
        // Charlie pays 600 for cab. Alice: 50%, Bob: 30%, Charlie: 20%
        System.out.println("\n=== Scenario 3: Percentage Split ===");
        System.out.println("Charlie pays 600 for cab. Alice: 50%, Bob: 30%, Charlie: 20%");

        Expense cab = new Expense(trip.getId(), charlie.getId(), 600.0, SplitType.PERCENTAGE);
        cab.setSplitDetails(Map.of(
                alice.getId(), 50.0,
                bob.getId(), 30.0,
                charlie.getId(), 20.0
        ));
        expenseService.addExpense(cab, allThree);

        printBalances(balanceMgr, "Alice", alice.getId());
        printBalances(balanceMgr, "Bob", bob.getId());
        printBalances(balanceMgr, "Charlie", charlie.getId());

        // --- Scenario 4: Edge case — zero amount ---
        System.out.println("\n=== Scenario 4: Zero Amount (should throw) ===");
        try {
            Expense bad = new Expense(trip.getId(), alice.getId(), 0, SplitType.EQUAL);
            expenseService.addExpense(bad, allThree);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 5: Edge case — percentages don't sum to 100 ---
        System.out.println("\n=== Scenario 5: Bad Percentage (should throw) ===");
        try {
            Expense badPct = new Expense(trip.getId(), alice.getId(), 500.0, SplitType.PERCENTAGE);
            badPct.setSplitDetails(Map.of(
                    alice.getId(), 50.0,
                    bob.getId(), 30.0,
                    charlie.getId(), 10.0
            ));
            expenseService.addExpense(badPct, allThree);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 6: Edge case — exact amounts don't match total ---
        System.out.println("\n=== Scenario 6: Bad Exact Split (should throw) ===");
        try {
            Expense badExact = new Expense(trip.getId(), bob.getId(), 500.0, SplitType.EXACT);
            badExact.setSplitDetails(Map.of(
                    alice.getId(), 100.0,
                    bob.getId(), 100.0,
                    charlie.getId(), 100.0
            ));
            expenseService.addExpense(badExact, allThree);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 7: Settle Balance ---
        // After all expenses: Alice owes Bob 100, Bob owes Charlie 320 (from scenario 3 output)
        // Bob settles 100 with Charlie
        System.out.println("\n=== Scenario 7: Settle Balance ===");
        System.out.println("Before settle:");
        printBalances(balanceMgr, "Bob", bob.getId());
        printBalances(balanceMgr, "Charlie", charlie.getId());

        // Bob owes Charlie 320 (Bob's view of Charlie is negative? Let's check)
        // From Scenario 3: Bob balances: {Alice=-100, Charlie=320} → Charlie owes Bob 320
        // Charlie balances: {Alice=0, Bob=-320} → Charlie owes Bob 320
        // So Charlie settles 320 with Bob
        System.out.println("\nCharlie settles 320 with Bob");
        expenseService.settleBalance(charlie.getId(), bob.getId(), 320.0);

        printBalances(balanceMgr, "Bob", bob.getId());
        printBalances(balanceMgr, "Charlie", charlie.getId());

        // --- Scenario 8: Settle partial ---
        System.out.println("\n=== Scenario 8: Partial Settle ===");
        System.out.println("Before settle:");
        printBalances(balanceMgr, "Alice", alice.getId());
        printBalances(balanceMgr, "Bob", bob.getId());

        // Alice owes Bob 100 (Alice view of Bob = 100, meaning Bob owes Alice 100)
        // Bob view of Alice = -100, meaning Bob owes Alice 100
        // So Bob settles 50 with Alice
        System.out.println("\nBob settles 50 with Alice");
        expenseService.settleBalance(bob.getId(), alice.getId(), 50.0);

        printBalances(balanceMgr, "Alice", alice.getId());
        printBalances(balanceMgr, "Bob", bob.getId());

        // --- Scenario 9: Edge case — settle with yourself ---
        System.out.println("\n=== Scenario 9: Settle with self (should throw) ===");
        try {
            expenseService.settleBalance(alice.getId(), alice.getId(), 100.0);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 10: Edge case — settle more than owed ---
        System.out.println("\n=== Scenario 10: Settle more than owed (should throw) ===");
        try {
            expenseService.settleBalance(bob.getId(), alice.getId(), 9999.0);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void printBalances(BalanceMgr balanceMgr, String name, String userId) {
        Map<String, Double> balances = balanceMgr.getBalancesForUser(userId);
        System.out.println(name + " balances: " + balances);
    }
}
