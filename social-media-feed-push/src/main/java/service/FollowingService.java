package service;

import entity.Post;
import exception.UserNotFoundException;
import manager.FeedMgr;
import manager.FollowMgr;
import manager.PostMgr;
import manager.UserMgr;

import java.util.List;

public class FollowingService {

    private final UserMgr userMgr;
    private final FollowMgr followMgr;
    private final FeedMgr feedMgr;
    private final PostMgr postMgr;

    public FollowingService(UserMgr userMgr, FollowMgr followMgr, FeedMgr feedMgr, PostMgr postMgr) {
        this.userMgr = userMgr;
        this.followMgr = followMgr;
        this.feedMgr = feedMgr;
        this.postMgr = postMgr;
    }

    public void follow(String userId, String followId) {
        if (userId.equals(followId)) {
            throw new IllegalArgumentException("User " + userId + " cannot follow self " + followId);
        }

        if (!userMgr.existById(userId)) {
            throw new UserNotFoundException("User cannot be found with user id " + userId);
        }
        if (!userMgr.existById(followId)) {
            throw new UserNotFoundException("User cannot be found with user id " + followId);
        }

        followMgr.save(userId, followId);

        // backfill: load followee's existing posts into follower's inbox
        List<Post> posts = postMgr.findByPostedBy(followId);
        if (!posts.isEmpty()) {
            feedMgr.backfill(userId, posts);
        }
    }

    public void unFollow(String userId, String followId) {
        if (!userMgr.existById(userId)) {
            throw new UserNotFoundException("User cannot be found with user id " + userId);
        }
        if (!userMgr.existById(followId)) {
            throw new UserNotFoundException("User cannot be found with user id " + followId);
        }

        followMgr.delete(userId, followId);

        // cleanup: remove unfollowed user's posts from inbox
        feedMgr.removePostsByUser(userId, followId);
    }
}
