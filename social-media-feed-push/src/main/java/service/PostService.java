package service;

import entity.Post;
import exception.PostDoesNotExistException;
import exception.UserNotFoundException;
import manager.FeedMgr;
import manager.FollowMgr;
import manager.PostMgr;
import manager.UserMgr;

import java.util.Set;

public class PostService {
    private final PostMgr postMgr;
    private final UserMgr userMgr;
    private final FollowMgr followMgr;
    private final FeedMgr feedMgr;

    public PostService(PostMgr postMgr, UserMgr userMgr, FollowMgr followMgr, FeedMgr feedMgr) {
        this.postMgr = postMgr;
        this.userMgr = userMgr;
        this.followMgr = followMgr;
        this.feedMgr = feedMgr;
    }

    public Post createPost(Post post) {
        if (!userMgr.existById(post.getPostedBy())) {
            throw new UserNotFoundException("User cannot be found with user id " + post.getPostedBy());
        }
        postMgr.save(post);

        // fan-out: push to every follower's inbox
        Set<String> followers = followMgr.getFollowers(post.getPostedBy());
        for (String followerId : followers) {
            feedMgr.push(followerId, post);
        }

        return post;
    }

    public void removePost(String id) {
        if (!postMgr.existsById(id)) {
            throw new PostDoesNotExistException("The post id does not exist " + id);
        }

        Post post = postMgr.findById(id);

        // fan-out: remove from every follower's inbox
        Set<String> followers = followMgr.getFollowers(post.getPostedBy());
        for (String followerId : followers) {
            feedMgr.remove(followerId, id);
        }

        postMgr.delete(id);
    }
}
