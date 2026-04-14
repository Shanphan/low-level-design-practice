# Social Media Feed (Push Model) — LLD Machine Coding

Push-based feed system. Companion to the pull-model implementation. Built for Meesho SDE-3 interview practice.

## Requirements

1. Create/delete users
2. Create/delete posts
3. Follow/unfollow users
4. Get feed — top K posts from followed users, sorted most recent first
5. Pagination on feed (cursor-based)

## Architecture

```
Main.java (driver)
    |
    v
Service Layer
    UserService       — create/remove user
    PostService       — create/remove post + fan-out to inboxes
    FollowingService  — follow (backfill) / unfollow (cleanup)
    FeedService       — getFeed(userId, limit, cursor) — reads inbox
    |
    v
Manager Layer
    UserMgr     — Map<String, User>
    PostMgr     — Map<String, Post>
    FollowMgr   — Map<String, Set<String>> following + followers (both directions)
    FeedMgr     — Map<String, LinkedList<Post>> feedInbox (precomputed per-user)
    |
    v
Entity Layer
    User        — id, name
    Post        — id, content, postedBy, postedTime
```

## Pull vs Push — Key Difference

### Pull (other module)
```
getFeed(userId) → get followed users → collect their posts → sort → limit
```
Compute at read time. Simple. O(followers x posts) per read.

### Push (this module)
```
createPost(post) → find all followers → push to each follower's inbox
getFeed(userId)  → read precomputed inbox
```
Compute at write time. Fast reads. O(followers) per write.

## How Push Works

### Fan-out on Write (createPost)
```
Alice creates a post
    → FollowMgr.getFollowers("alice") → {bob, charlie, dave}
    → FeedMgr.push("bob", post)
    → FeedMgr.push("charlie", post)
    → FeedMgr.push("dave", post)
```
Each follower's inbox gets the post prepended (LinkedList.addFirst — O(1)).

### Backfill on Follow
```
Bob follows Alice (Alice already has 50 posts)
    → PostMgr.findByPostedBy("alice") → [50 posts]
    → FeedMgr.backfill("bob", posts) → merge + re-sort inbox
```
Without backfill, Bob would only see Alice's future posts.

### Cleanup on Unfollow
```
Bob unfollows Alice
    → FeedMgr.removePostsByUser("bob", "alice") → remove all Alice's posts from Bob's inbox
```

### Fan-out on Delete
```
Alice deletes a post
    → FollowMgr.getFollowers("alice") → {bob, charlie}
    → FeedMgr.remove("bob", postId)
    → FeedMgr.remove("charlie", postId)
```

### getFeed — Trivial Read
```java
List<Post> inbox = feedMgr.getInbox(userId);  // already sorted
return inbox.stream()
        .filter(p -> p.getPostedTime().isBefore(cursor))
        .limit(limit)
        .toList();
```
No sorting. No collecting from multiple users. Just read and paginate.

## Why LinkedList for Inbox

`addFirst` is O(1) on LinkedList vs O(n) on ArrayList (shifts all elements). Feed inboxes are prepend-heavy — every new post inserts at front.

## Cost Comparison

| Operation | Pull Model | Push Model |
|-----------|-----------|------------|
| Create post | O(1) — just save | O(F) — fan-out to F followers |
| Get feed | O(n log n) — collect + sort | O(limit) — read inbox |
| Delete post | O(1) — next read skips it | O(F) — remove from F inboxes |
| Follow | O(1) — add to set | O(P) — backfill P posts |
| Unfollow | O(1) — remove from set | O(P) — remove P posts from inbox |

**Push wins when reads >> writes.** Social media feeds are read-heavy — users scroll 100x more than they post.

## Edge Cases Handled

| Edge Case | Where | Behavior |
|-----------|-------|----------|
| Post before any followers | PostService.createPost | No fan-out (empty follower set) |
| Follow user with existing posts | FollowingService.follow | Backfill into inbox |
| Unfollow | FollowingService.unFollow | Remove their posts from inbox |
| Delete post | PostService.removePost | Fan-out remove from all follower inboxes |
| Self-follow | FollowingService.follow | IllegalArgumentException |
| Follow nonexistent user | FollowingService.follow | UserNotFoundException |
| Feed for nonexistent user | FeedService.getFeed | UserNotFoundException |
| Empty feed (no follows) | FeedService.getFeed | Returns empty list |
| Delete nonexistent post | PostService.removePost | PostDoesNotExistException |

## Demo Scenarios (Main.java)

1. Bob follows Alice + Charlie — **backfill** loads their existing posts
2. Alice posts after Bob follows — **fan-out** pushes to Bob's inbox
3. Limit = 2 (top K)
4. Cursor pagination (posts older than 2h)
5. Delete post — removed from Bob's inbox via fan-out
6. Unfollow Alice — her posts cleaned from Bob's inbox
7. Dave follows nobody — empty feed
8. Self-follow throws
9. Follow nonexistent user throws
10. Feed for nonexistent user throws
11. Delete nonexistent post throws

## Scaling Discussion (Verbal)

### Celebrity Problem
A user with 10M followers posts once → 10M inbox writes. This is the **write amplification** problem.

**Solution: Hybrid model**
- Normal users (< 100K followers) → push model
- Celebrities (> 100K followers) → pull model
- At read time: merge precomputed inbox + pull celebrity posts

This is what Twitter, Instagram, and Facebook actually do.

### FollowMgr: Two Maps
Push model requires both directions:
- `following[A] = {B, C}` — A follows B and C (used for backfill)
- `followers[B] = {A, D}` — A and D follow B (used for fan-out)

Pull model only needs `following`. Push needs both.

## Concurrency (Follow-up)

If interviewer asks "make it thread-safe":
1. `ConcurrentHashMap` in all managers
2. FeedMgr inbox: `Collections.synchronizedList(new LinkedList<>())` or use `ConcurrentLinkedDeque`
3. Fan-out can be parallelized with `CompletableFuture` or thread pool
4. Backfill needs synchronization — sort after addAll must be atomic

## Project Structure

```
src/main/java/
    Main.java
    entity/
        User.java
        Post.java
    manager/
        UserMgr.java
        PostMgr.java
        FollowMgr.java
        FeedMgr.java          ← NEW: precomputed inbox storage
    service/
        UserService.java
        PostService.java       ← fan-out on create/delete
        FollowingService.java  ← backfill on follow, cleanup on unfollow
        FeedService.java       ← trivial inbox read
        IdGenerator.java
    exception/
        UserNotFoundException.java
        PostDoesNotExistException.java
```
