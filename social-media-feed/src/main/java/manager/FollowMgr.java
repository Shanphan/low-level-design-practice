package manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FollowMgr {

    private Map<String, Set<String>> following;

    public FollowMgr() {
        this.following = new HashMap<>();
    }


    public String save(String userId, String followingId) {
        following.computeIfAbsent(userId, k-> new HashSet<>());
        following.get(userId).add(followingId);
        return followingId;
    }

    public void saveAll(String userId, Set<String> followingList) {
        following.computeIfAbsent(userId, k-> new HashSet<>());
        following.get(userId).addAll(followingList);
    }

    public void delete(String userId, String followingId) {
        following.get(userId).remove(followingId);
    }

    public Set<String> findById(String userId) {
        return following.get(userId) == null ? Set.of() :  following.get(userId);
    }


}
