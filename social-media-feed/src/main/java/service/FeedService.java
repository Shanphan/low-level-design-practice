package service;

import entity.Post;
import entity.User;
import exception.UserNotFoundException;
import manager.FollowMgr;
import manager.PostMgr;
import manager.UserMgr;

import java.time.LocalDateTime;
import java.util.*;

public class FeedService {

    private final UserMgr userMgr;
    private final PostMgr postMgr;
    private final FollowMgr followMgr;


    public FeedService(UserMgr userMgr, PostMgr postMgr, FollowMgr followMgr) {
        this.userMgr = userMgr;
        this.postMgr = postMgr;
        this.followMgr = followMgr;
    }

    public List<Post> getFeed(String userId, int limit, LocalDateTime cursor) {

        boolean userExists = userMgr.existById(userId);

        if(!userExists) {
            throw new UserNotFoundException("USer cannot be found with user id "  + userId);
        }

        //1. Find all followers and find all posts by follower sort by TimeStamp and return top limit
        Set<String> following = followMgr.findById(userId);
        if(following.isEmpty()) {
            return List.of();
        }

        //2. Get all post by follower and save it in a DS sort by timestamp
        //PriorityQueue<Post> postQueue = new PriorityQueue<>(Comparator.comparing(Post::getPostedTime).reversed());
        //PriorityQueue<Post> posts = new PriorityQueue<>((a,b) -> b.getPostedTime().compareTo(a.getPostedTime()));
        List<Post> allPosts = new ArrayList<>();

        for(String followingId : following) {
            List<Post> posts = postMgr.findByPostedBy(followingId);
            allPosts.addAll(posts);

        }

        return allPosts.stream()
                .filter(p -> cursor == null || p.getPostedTime().isBefore(cursor))
                .sorted(Comparator.comparing(Post::getPostedTime).reversed())
                .limit(limit)
                .toList();


    }

    public List<Post> getFeedPriorityQueue(String userId, int limit, LocalDateTime cursor) {

        boolean userExists = userMgr.existById(userId);

        if(!userExists) {
            throw new UserNotFoundException("USer cannot be found with user id "  + userId);
        }

        //1. Find all followers and find all posts by follower sort by TimeStamp and return top limit
        Set<String> following = followMgr.findById(userId);
        if(following.isEmpty()) {
            return List.of();
        }

        //2. Get all post by follower and save it in a DS sort by timestamp
        //PriorityQueue<Post> postQueue = new PriorityQueue<>(Comparator.comparing(Post::getPostedTime).reversed());
        //PriorityQueue<Post> posts = new PriorityQueue<>((a,b) -> b.getPostedTime().compareTo(a.getPostedTime()));
        List<Post> allPosts = new ArrayList<>();

        for(String followingId : following) {
            List<Post> posts = postMgr.findByPostedBy(followingId);
            allPosts.addAll(posts);

        }

        PriorityQueue<Post> pq = new PriorityQueue<>(Comparator.comparing(Post::getPostedTime));

        for(Post post : allPosts) {

            if(pq.size() < limit) {
                pq.offer(post);
            } else if (pq.peek().getPostedTime().isBefore(post.getPostedTime())){
                pq.poll();
                pq.offer(post);

            }
        }

        List<Post> recentPosts = new ArrayList<>(pq);
        recentPosts.sort(Comparator.comparing(Post::getPostedTime).reversed());

        return recentPosts;


    }
}
