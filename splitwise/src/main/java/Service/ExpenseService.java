package Service;

import entity.Expense;
import entity.Group;
import manager.BalanceMgr;
import manager.ExpenseMgr;
import manager.GroupMgr;
import strategy.SplitStrategy;
import strategy.SplitStrategyFactory;

import java.util.*;;

public class ExpenseService {

    private final ExpenseMgr expenseMgr;
    private final GroupMgr groupMgr;
    private final BalanceMgr balanceMgr;

    public ExpenseService(ExpenseMgr expenseMgr, GroupMgr groupMgr, BalanceMgr balanceMgr) {
        this.expenseMgr = expenseMgr;
        this.groupMgr = groupMgr;
        this.balanceMgr = balanceMgr;
    }


    public void addExpense(Expense expense, Set<String> participants) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be empty");
        }
        if (expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        SplitStrategy strategy = SplitStrategyFactory.createStrategy(expense.getSplitType());
        Map<String, Double> splits = strategy.splits(expense.getAmount(), expense.getSplitDetails(), participants);

        for (Map.Entry<String, Double> entry : splits.entrySet()) {
            if (entry.getKey().equals(expense.getPaidByUserId())) continue;
            balanceMgr.update(expense.getPaidByUserId(), entry.getKey(), entry.getValue());
        }

        expenseMgr.save(expense);
    }

    public void settleBalance(String payerId, String payeeId, double amount) {
        if (payerId.equals(payeeId)) {
            throw new IllegalArgumentException("Cannot settle with yourself");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Settlement amount must be greater than 0");
        }

        Map<String, Double> payerBalances = balanceMgr.getBalancesForUser(payerId);
        double owed = payerBalances.getOrDefault(payeeId, 0.0);

        // payer owes payee → owed is negative
        // settling more than you owe makes no sense
        if (owed >= 0) {
            throw new IllegalArgumentException(payerId + " does not owe " + payeeId + " anything");
        }
        if (amount > Math.abs(owed)) {
            throw new IllegalArgumentException("Settlement amount " + amount + " exceeds debt of " + Math.abs(owed));
        }

        // payer pays payee → payee is owed less by payer
        balanceMgr.update(payeeId, payerId, -amount);

    }

    /**
     * Simplify debts — minimize number of transactions to settle all balances.
     * Greedy: match max creditor with max debtor, settle min of the two, repeat.
     */
    public List<String> minTransactions() {
        // Step 1: compute net balance per person
        Map<String, Double> netBalance = new HashMap<>();
        // balanceSheet: A → {B: +300} means B owes A 300
        // So A's net goes up by 300, B's net goes down by 300
        // We only process one direction (positive entries) to avoid double counting
        Map<String, Map<String, Double>> allBalances = balanceMgr.getBalanceSheet();

        for (var entry : allBalances.entrySet()) {
            String user = entry.getKey();
            for (var inner : entry.getValue().entrySet()) {
                double amount = inner.getValue();
                if (amount > 0) {
                    // user is owed 'amount' by inner.getKey()
                    netBalance.merge(user, amount, Double::sum);
                    netBalance.merge(inner.getKey(), -amount, Double::sum);
                }
            }
        }

        // Step 2: separate into creditors (positive) and debtors (negative)
        // max-heap by amount for creditors
        PriorityQueue<double[]> creditors = new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0]));
        // max-heap by abs amount for debtors
        PriorityQueue<double[]> debtors = new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0]));

        // map user to index for heap storage
        List<String> users = new ArrayList<>(netBalance.keySet());
        Map<String, Integer> userIndex = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            userIndex.put(users.get(i), i);
        }

        for (var entry : netBalance.entrySet()) {
            double bal = entry.getValue();
            int idx = userIndex.get(entry.getKey());
            if (bal > 0.01) {
                creditors.offer(new double[]{bal, idx});
            } else if (bal < -0.01) {
                debtors.offer(new double[]{Math.abs(bal), idx});
            }
        }

        // Step 3: greedy match + collect simplified pairs
        List<String> transactions = new ArrayList<>();
        // creditorId, debtorId, amount
        List<double[]> simplifiedDebts = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            double[] maxCreditor = creditors.poll();
            double[] maxDebtor = debtors.poll();

            String creditorName = users.get((int) maxCreditor[1]);
            String debtorName = users.get((int) maxDebtor[1]);
            double settle = Math.min(maxCreditor[0], maxDebtor[0]);

            transactions.add(debtorName + " pays " + creditorName + " " + Math.round(settle * 100.0) / 100.0);
            simplifiedDebts.add(new double[]{maxCreditor[1], maxDebtor[1], settle});

            double creditorRemaining = maxCreditor[0] - settle;
            double debtorRemaining = maxDebtor[0] - settle;

            if (creditorRemaining > 0.01) {
                creditors.offer(new double[]{creditorRemaining, maxCreditor[1]});
            }
            if (debtorRemaining > 0.01) {
                debtors.offer(new double[]{debtorRemaining, maxDebtor[1]});
            }
        }

        // Step 4: rewrite balance sheet with simplified debts
        balanceMgr.clear();
        for (double[] debt : simplifiedDebts) {
            String creditor = users.get((int) debt[0]);
            String debtor = users.get((int) debt[1]);
            double amount = debt[2];
            // creditor is owed 'amount' by debtor
            balanceMgr.update(creditor, debtor, amount);
        }

        return transactions;
    }
}
