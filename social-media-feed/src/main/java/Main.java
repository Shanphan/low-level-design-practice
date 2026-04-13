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
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /**
         *  1. Basic feed (follow + retrieve)
         *   2. Limit (top K)
         *   3. Cursor pagination (older posts)
         *   4. Deleted post disappears from feed
         *   5. Unfollow removes their posts from feed
         *   6. Empty feed (no follows)
         *   7. Self-follow (throws)
         *   8. Follow nonexistent user (throws)
         *   9. Feed for nonexistent user (throws)
         *   10. Delete nonexistent post (throws)
         *
         *   Run it. Expected behavior:
         *   - Bob's feed in Scenario 1 should show 5 posts (3 Alice + 2 Charlie), most recent first
         *   - Scenario 2 shows only top 2
         *   - Scenario 3 shows only posts older than 2h
         *   - After delete, Charlie's "will delete" post is gone
         *   - After unfollow Alice, only Charlie's post remains
         * */

        // --- Wire dependencies ---
        UserMgr userMgr = new UserMgr();
        PostMgr postMgr = new PostMgr();
        FollowMgr followMgr = new FollowMgr();

        UserService userService = new UserService(userMgr);
        PostService postService = new PostService(postMgr, userMgr);
        FeedService feedService = new FeedService(userMgr, postMgr, followMgr);
        FollowingService followingService = new FollowingService(userMgr, followMgr);

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

        // --- Create posts with varied timestamps ---
        LocalDateTime now = LocalDateTime.now();
        postService.createPost(new Post("Alice post #1 (oldest)", alice.getId(), now.minusHours(10)));
        postService.createPost(new Post("Alice post #2", alice.getId(), now.minusHours(5)));
        postService.createPost(new Post("Alice post #3 (newest)", alice.getId(), now.minusMinutes(30)));

        postService.createPost(new Post("Bob post #1", bob.getId(), now.minusHours(8)));
        postService.createPost(new Post("Bob post #2 (newest)", bob.getId(), now.minusMinutes(10)));

        Post charlieDeletable = postService.createPost(new Post("Charlie will delete this", charlie.getId(), now.minusHours(2)));
        postService.createPost(new Post("Charlie stays", charlie.getId(), now.minusHours(1)));

        postService.createPost(new Post("Dave post (not followed)", dave.getId(), now.minusMinutes(5)));

        // --- Scenario 1: Bob follows Alice + Charlie ---
        System.out.println("\n=== Scenario 1: Bob follows Alice + Charlie, get feed ===");
        followingService.follow(bob.getId(), alice.getId());
        followingService.follow(bob.getId(), charlie.getId());
        List<Post> feed = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed", feed);

        // --- Scenario 2: Limit = 2 (top newest) ---
        System.out.println("\n=== Scenario 2: Limit = 2 (top 2 newest) ===");
        List<Post> topTwo = feedService.getFeed(bob.getId(), 2, now.plusMinutes(1));
        printFeed("Bob's top 2", topTwo);

        // --- Scenario 3: Cursor pagination (posts older than 2h ago) ---
        System.out.println("\n=== Scenario 3: Cursor = 2 hours ago ===");
        List<Post> older = feedService.getFeed(bob.getId(), 10, now.minusHours(2));
        printFeed("Bob's older posts", older);

        // --- Scenario 4: Deleted post disappears ---
        System.out.println("\n=== Scenario 4: Delete Charlie's post ===");
        postService.removePost(charlieDeletable.getId());
        List<Post> afterDelete = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed after delete", afterDelete);

        // --- Scenario 5: Unfollow removes their posts ---
        System.out.println("\n=== Scenario 5: Bob unfollows Alice ===");
        followingService.unFollow(bob.getId(), alice.getId());
        List<Post> afterUnfollow = feedService.getFeed(bob.getId(), 10, now.plusMinutes(1));
        printFeed("Bob's feed after unfollow", afterUnfollow);

        // --- Scenario 6: User follows nobody ---
        System.out.println("\n=== Scenario 6: Dave follows nobody ===");
        List<Post> emptyFeed = feedService.getFeed(dave.getId(), 10, now.plusMinutes(1));
        printFeed("Dave's feed (should be empty)", emptyFeed);

        // --- Scenario 7: Self-follow throws ---
        System.out.println("\n=== Scenario 7: Self-follow (should throw) ===");
        try {
            followingService.follow(alice.getId(), alice.getId());
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 8: Follow nonexistent user throws ---
        System.out.println("\n=== Scenario 8: Follow nonexistent user (should throw) ===");
        try {
            followingService.follow(alice.getId(), "USER-9999");
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 9: Feed for nonexistent user throws ---
        System.out.println("\n=== Scenario 9: Feed for nonexistent user (should throw) ===");
        try {
            feedService.getFeed("USER-9999", 5, now);
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Scenario 10: Delete nonexistent post throws ---
        System.out.println("\n=== Scenario 10: Delete nonexistent post (should throw) ===");
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
