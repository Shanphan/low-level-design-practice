package Manager;

import entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserMgr {

    private final Map<String, User> users;

    public UserMgr() {
        this.users = new ConcurrentHashMap<>();
    }

    public User save(User user) {
        return users.put(user.getId(), user);
    }

    public User findById(String id) {
        return users.get(id);
    }
}
