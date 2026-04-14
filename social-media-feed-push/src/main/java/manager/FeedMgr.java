package manager;

import entity.Post;

import java.util.*;

public class FeedMgr {

    // userId → precomputed feed inbox (newest first)
    private Map<String, LinkedList<Post>> feedInbox;

    public FeedMgr() {
        this.feedInbox = new HashMap<>();
    }

    public void push(String userId, Post post) {
        feedInbox.computeIfAbsent(userId, k -> new LinkedList<>()).addFirst(post);
    }

    public void remove(String userId, String postId) {
        List<Post> inbox = feedInbox.get(userId);
        if (inbox != null) {
            inbox.removeIf(p -> p.getId().equals(postId));
        }
    }

    public void removePostsByUser(String feedOwnerId, String postedByUserId) {
        List<Post> inbox = feedInbox.get(feedOwnerId);
        if (inbox != null) {
            inbox.removeIf(p -> p.getPostedBy().equals(postedByUserId));
        }
    }

    public void backfill(String feedOwnerId, List<Post> posts) {
        List<Post> inbox = feedInbox.computeIfAbsent(feedOwnerId, k -> new LinkedList<>());
        inbox.addAll(posts);
        inbox.sort(Comparator.comparing(Post::getPostedTime).reversed());
    }

    public List<Post> getInbox(String userId) {
        return feedInbox.getOrDefault(userId, new LinkedList<>());
    }
}
