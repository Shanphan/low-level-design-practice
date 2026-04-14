package manager;

import entity.User;

import java.util.HashMap;
import java.util.Map;

public class UserMgr {

    private final Map<String, User> users = new HashMap<>();

    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User findById(String id) {
        return users.get(id);
    }

    public boolean existsById(String id) {
        return users.containsKey(id);
    }
}
