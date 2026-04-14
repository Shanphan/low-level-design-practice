package manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FollowMgr {

    private Map<String, Set<String>> following;
    private Map<String, Set<String>> followers;

    public FollowMgr() {
        this.following = new HashMap<>();
        this.followers = new HashMap<>();
    }


    public String save(String userId, String followingId) {
        following.computeIfAbsent(userId, k-> new HashSet<>()).add(followingId);
        followers.computeIfAbsent(followingId, k-> new HashSet<>()).add(userId);
        return followingId;
    }

    public void saveAll(String userId, Set<String> followingList) {
        following.computeIfAbsent(userId, k-> new HashSet<>());
        following.get(userId).addAll(followingList);
    }

    public void delete(String userId, String followingId) {
        following.get(userId).remove(followingId);
        followers.get(followingId).remove(userId);
    }

    public Set<String> findById(String userId) {
        return following.getOrDefault(userId, Set.of());
    }

    public Set<String> getFollowers(String userId) {
        return followers.getOrDefault(userId, Set.of());
    }


}
