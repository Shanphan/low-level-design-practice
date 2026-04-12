package Service;

import entity.Expense;
import entity.Group;
import manager.BalanceMgr;
import manager.ExpenseMgr;
import manager.GroupMgr;
import strategy.SplitStrategy;
import strategy.SplitStrategyFactory;

import java.util.Map;
import java.util.Set;

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
}
