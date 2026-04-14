package service;

import entity.User;
import exception.UserNotFoundException;
import manager.UserMgr;

public class UserService {

    private final UserMgr userMgr;

    public UserService(UserMgr userMgr) {
        this.userMgr = userMgr;
    }

    public User createUser(User user) {
        return userMgr.save(user);
    }

    public void addBalance(String userId, double amount) {
        User user = userMgr.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        user.setBalance(user.getBalance() + amount);
    }
}
