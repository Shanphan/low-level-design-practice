import entity.Post;
import entity.User;
import manager.FeedMgr;
import manager.FollowMgr;
import manager.PostMgr;
import manager.UserMgr;
import service.FeedService;
import service.FollowingService;
import service.PostService;
import service.UserService;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /**
         *  1. Follow then retrieve (backfill test)
         *  2. Post AFTER follow (fan-out test)
         *  3. Limit (top K)
         *  4. Cursor pagination (older posts)
         *  5. Deleted post disappears from feed
         *  6. Unfollow removes their posts from feed
         *  7. Empty feed (no follows)
         *  8. Self-follow (throws)
         *  9. Follow nonexistent user (throws)
         *  10. Feed for nonexistent user (throws)
         *  11. Delete nonexistent post (throws)
         */

        // --- Wire dependencies ---
        UserMgr userMgr = new UserMgr();
        PostMgr postMgr = new PostMgr();
        FollowMgr followMgr = new FollowMgr();
        FeedMgr feedMgr = new FeedMgr();

        UserService userService = new UserService(userMgr);
        PostService postService = new PostService(postMgr, userMgr, followMgr, feedMgr);
        FeedService feedService = new FeedService(userMgr, feedMgr);
        FollowingService followingService = new FollowingService(userMgr, followMgr, feedMgr, postMgr);

        // --- Create users ---
        User alice = userService.createUser(new User("Alice"));
        User bob = userService.createUser(new User("Bob"));
        User charlie = userService.createUser(new User("Charlie"));
        User dave = userService.createUser(new User("Dave"));

        System.out.println("=== Users Created ===");
        System.out.println(alice.getId() + " = Alice");
        System.out.println(bob.getId() + " = Bob");
        System.out.println(charlie.getId() + " = Charlie");
        System.out.println(dave.getId() + " = Dave");

        // --- Create posts BEFORE follow (these get backfilled on follow) ---
        LocalDateTime now = LocalDateTime.now();
        postService.createPost(new Post("Alice post #1 (oldest)", alice.getId(), now.minusHours(10)));
        postService.createPost(new Post("Alice post #2", alice.getId(), now.minusHours(5)));
        postService.createPost(new Post("Alice post #3", alice.getId(), now.minusMinutes(30)));

        Post charlieDeletable = postService.createPost(new Post("Charlie will delete this", charlie.getId(), now.minusHours(2)));
        postService.createPost(new Post("Charlie stays", charlie.getId(), now.minusHours(1)));

        postService.createPost(new Post("Dave post (not followed)", dave.getId(), now.minusMinutes(5)));

        // --- Scenario 1: Bob follows Alice + Charlie — backfill kicks in ---
        System.out.println("\n=== Scenario 1: Bob follows Alice + Charlie (backfill) ===");
        followingService.follow(bob.getId(), alice.getId());
        followingService.follow(bob.getId(), charlie.getId());
        List<Post> feed = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed (backfilled)", feed);

        // --- Scenario 2: Post AFTER follow — fan-out pushes to Bob's inbox ---
        System.out.println("\n=== Scenario 2: Alice posts AFTER Bob follows (fan-out) ===");
        postService.createPost(new Post("Alice post #4 (newest, fan-out)", alice.getId(), now.minusMinutes(5)));
        List<Post> afterFanOut = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed (with fan-out post)", afterFanOut);

        // --- Scenario 3: Limit = 2 (top newest) ---
        System.out.println("\n=== Scenario 3: Limit = 2 (top 2 newest) ===");
        List<Post> topTwo = feedService.getFeed(bob.getId(), 2, now.plusMinutes(1));
        printFeed("Bob's top 2", topTwo);

        // --- Scenario 4: Cursor pagination (posts older than 2h ago) ---
        System.out.println("\n=== Scenario 4: Cursor = 2 hours ago ===");
        List<Post> older = feedService.getFeed(bob.getId(), 10, now.minusHours(2));
        printFeed("Bob's older posts", older);

        // --- Scenario 5: Deleted post disappears ---
        System.out.println("\n=== Scenario 5: Delete Charlie's post ===");
        postService.removePost(charlieDeletable.getId());
        List<Post> afterDelete = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed after delete", afterDelete);

        // --- Scenario 6: Unfollow removes their posts ---
        System.out.println("\n=== Scenario 6: Bob unfollows Alice ===");
        followingService.unFollow(bob.getId(), alice.getId());
        List<Post> afterUnfollow = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed after unfollow", afterUnfollow);

        // --- Scenario 7: User follows nobody ---
        System.out.println("\n=== Scenario 7: Dave follows nobody ===");
        List<Post> emptyFeed = feedService.getFeed(dave.getId(), 10, now.plusMinutes(1));
        printFeed("Dave's feed (should be empty)", emptyFeed);

        // --- Scenario 8: Self-follow throws ---
        System.out.println("\n=== Scenario 8: Self-follow (should throw) ===");
        try {
            followingService.follow(alice.getId(), alice.getId());
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 9: Follow nonexistent user throws ---
        System.out.println("\n=== Scenario 9: Follow nonexistent user (should throw) ===");
        try {
            followingService.follow(alice.getId(), "USER-9999");
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 10: Feed for nonexistent user throws ---
        System.out.println("\n=== Scenario 10: Feed for nonexistent user (should throw) ===");
        try {
            feedService.getFeed("USER-9999", 5, now);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 11: Delete nonexistent post throws ---
        System.out.println("\n=== Scenario 11: Delete nonexistent post (should throw) ===");
        try {
            postService.removePost("POST-9999");
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void printFeed(String label, List<Post> feed) {
        System.out.println(label + " (" + feed.size() + " posts):");
        if (feed.isEmpty()) {
            System.out.println("  <empty>");
            return;
        }
        for (Post p : feed) {
            System.out.println("  " + p);
        }
    }
}
