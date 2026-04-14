package service;

import entity.User;
import manager.UserMgr;

public class UserService {

    private final UserMgr userMgr;

    public UserService(UserMgr userMgr) {
        this.userMgr = userMgr;
    }

    public User createUser(User user) {
        return userMgr.save(user);
    }

    public void removeUser (String id ) {
        userMgr.deleteById(id);
    }


}
