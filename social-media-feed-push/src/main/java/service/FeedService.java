package service;

import entity.Post;
import exception.UserNotFoundException;
import manager.FeedMgr;
import manager.UserMgr;

import java.time.LocalDateTime;
import java.util.List;

public class FeedService {

    private final UserMgr userMgr;
    private final FeedMgr feedMgr;

    public FeedService(UserMgr userMgr, FeedMgr feedMgr) {
        this.userMgr = userMgr;
        this.feedMgr = feedMgr;
    }

    public List<Post> getFeed(String userId, int limit, LocalDateTime cursor) {
        if (!userMgr.existById(userId)) {
            throw new UserNotFoundException("User cannot be found with user id " + userId);
        }

        // inbox is already sorted newest-first from push
        List<Post> inbox = feedMgr.getInbox(userId);

        return inbox.stream()
                .filter(p -> cursor == null || p.getPostedTime().isBefore(cursor))
                .limit(limit)
                .toList();
    }
}
