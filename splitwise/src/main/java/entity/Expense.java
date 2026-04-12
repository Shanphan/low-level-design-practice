package entity;

import Service.IdGenerator;

import java.util.HashMap;
import java.util.Map;

public class Expense {

    private String id;
    private String groupId;
    private String paidByUserId;
    private double amount;
    private SplitType splitType;
    private Map<String, Double> splitDetails; //based on strategy

    public Expense (String groupId, String paidByUserId, double amount, SplitType splitType) {
        this.id = IdGenerator.createId("EXPENSE");
        this.groupId = groupId;
        this.paidByUserId = paidByUserId;
        this.amount = amount;
        this.splitType = splitType;
        this.splitDetails = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getPaidByUserId() {
        return paidByUserId;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public Map<String, Double> getSplitDetails() {
        return splitDetails;
    }

    public void setSplitDetails(Map<String, Double> splitDetails) {
        this.splitDetails = splitDetails;
    }

    public double getAmount() {
        return amount;
    }

    public String getGroupId() {
        return groupId;
    }
}
