package manager;

import entity.Expense;

import java.util.HashMap;
import java.util.Map;

public class ExpenseMgr {

    private final Map<String, Expense> expenses;

    public ExpenseMgr() {
        this.expenses = new HashMap<>();
    }

    public Expense save(Expense expense) {
        expenses.put(expense.getId(), expense);
        return expense;
    }
}
