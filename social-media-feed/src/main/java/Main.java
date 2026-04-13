import entity.Post;
import entity.User;
import manager.FollowMgr;
import manager.PostMgr;
import manager.UserMgr;
import service.FeedService;
import service.FollowingService;
import service.PostService;
import service.UserService;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        UserMgr userMgr = new UserMgr();
        PostMgr postMgr = new PostMgr();
        FollowMgr followMgr = new FollowMgr();

        UserService userService = new UserService(userMgr);
        PostService postService = new PostService(postMgr, userMgr);
        FeedService feedService = new FeedService(userMgr, postMgr, followMgr);
        FollowingService followingService = new FollowingService(userMgr, followMgr);

        User u1 = new User("SP");
        User u2 = new User("TJ");
        userService.createUser(u1);
        userService.createUser(u2);

        Post p1 = new Post("Hi TJ ", u1.getId(), LocalDateTime.now());
        Post p2 = new Post("Hello TJ ", u1.getId(), LocalDateTime.now().minusHours(2));
        Post p3 = new Post("Hi SP ", u2.getId(), LocalDateTime.now());
        Post p4 = new Post("Hello SP ", u2.getId(), LocalDateTime.now().minusHours(5));

        postService.createPost(p1);
        postService.createPost(p2);
        postService.createPost(p3);
        postService.createPost(p4);

        followingService.follow(u1.getId(), "dad");
        followingService.follow(u2.getId(), u1.getId());

        System.out.println(feedService.getFeed(u1.getId(), 3, LocalDateTime.now()));
        System.out.println(feedService.getFeed(u1.getId(), 3, LocalDateTime.now().minusHours(1)));






    }
}
