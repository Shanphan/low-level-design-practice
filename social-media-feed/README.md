# Social Media Feed — LLD Machine Coding

Post and feed system. Built for Meesho SDE-3 interview practice.

*[Actually asked at Meesho SDE — Medium, LinkedIn (Pravash Jha)]*

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
    PostService       — create/remove post
    FollowingService  — follow/unfollow
    FeedService       — getFeed(userId, limit, cursor)
    |
    v
Manager Layer (repositories)
    UserMgr     — Map<String, User>
    PostMgr     — Map<String, Post>
    FollowMgr   — Map<String, Set<String>> following
    |
    v
Entity Layer
    User        — id, name
    Post        — id, content, postedBy, postedTime
```

## Key Design Decisions

### 1. No Follower Entity
Follow is a relationship, not a thing. Modeled as `Map<String, Set<String>> following` in `FollowMgr`. O(1) add/remove/lookup, no ceremony.

Only one direction stored (`following`). For push model or reverse lookup, add `followers` map — not needed for pull-model pagination.

### 2. Pull Model for Feed Generation

```
getFeed(userId) → get followed users → collect their posts → sort by time → limit
```

**Why pull over push:**
- Simpler in 90 minutes — no fan-out logic
- No duplicate storage (posts live in PostMgr only)
- Deletion is trivial — PostMgr removes the post, feed filter naturally excludes it
- Tradeoff: O(followers × posts) per feed read. Mention push as a scale-up optimization in interview.

### 3. Cursor-Based Pagination

```java
getFeed(userId, limit, cursor) → posts older than cursor, newest first
```

**Why cursor over offset:**
- Stable under new inserts — no duplicates or skips if new posts arrive between pages
- Client passes the timestamp of the last-seen post as the cursor
- First page: pass `LocalDateTime.now()` (or future) to get newest posts

### 4. Deletion Works Without Feed Rebuild
Pull model means the feed queries posts fresh every time. When a post is deleted from `PostMgr`, the next `getFeed` call simply doesn't find it. No stale references, no cleanup needed in the follow graph.

## Core Flow

```
createPost(post) → PostMgr.save
removePost(id)   → validate exists → PostMgr.delete

follow(a, b)     → validate both exist, a != b → following[a].add(b)
unfollow(a, b)   → validate both exist → following[a].remove(b)

getFeed(userId, limit, cursor)
    1. Validate user exists
    2. Get following[userId] → set of followed IDs
    3. If empty, return []
    4. Collect all posts from followed users
    5. Filter: post.time < cursor
    6. Sort by time descending
    7. Limit to top K
```

## Edge Cases Handled

| Edge Case | Where | Behavior |
|-----------|-------|----------|
| Self-follow | FollowingService.follow | IllegalArgumentException |
| Follow nonexistent user | FollowingService.follow | UserNotFoundException |
| Unfollow nonexistent user | FollowingService.unFollow | UserNotFoundException |
| Duplicate follow | FollowMgr (Set semantics) | Silently idempotent |
| Unfollow when not following | FollowMgr (Set.remove) | Silently no-op |
| Feed for nonexistent user | FeedService.getFeed | UserNotFoundException |
| Empty feed (no follows) | FeedService.getFeed | Returns empty list |
| Deleted post | PostMgr.delete + pull filter | Naturally excluded |
| Delete nonexistent post | PostService.removePost | PostDoesNotExistException |

## Demo Scenarios (Main.java)

1. Bob follows Alice + Charlie, retrieve full feed
2. Limit = 2 (top K)
3. Cursor pagination (posts older than 2h)
4. Delete post — disappears from feed
5. Unfollow Alice — her posts disappear from Bob's feed
6. Dave follows nobody — empty feed
7. Self-follow throws
8. Follow nonexistent user throws
9. Feed for nonexistent user throws
10. Delete nonexistent post throws

## Scaling Discussion (Verbal)

### Push Model for Heavy Users
Pull is O(followers × posts). For a user following 10,000 accounts, that's expensive every read. Switch to push:

```
createPost(post) → find all followers of postedBy → append to each follower's inbox
getFeed(userId) → read precomputed inbox, already sorted
```

**Tradeoff:** write amplification. A celebrity with 10M followers triggers 10M inbox writes per post. Hybrid model in production — push for normal users, pull for celebrities.

### Why Cursor Pagination Beats Offset
```
User on page 3 (offset=20), new post arrives at top
With offset: next page skips the bottom of current page (shifted down by 1)
With cursor: cursor pins to a timestamp, new posts don't affect what comes after

Cursor is what Twitter, Instagram, Facebook actually use.
```

## Concurrency (Follow-up)

If interviewer asks "make it thread-safe":
1. `ConcurrentHashMap` in all managers
2. `AtomicLong` already used in `IdGenerator`
3. For feed reads: `ConcurrentHashMap` handles concurrent reads/writes naturally. No locking needed — pull model has no cross-entity state to synchronize.
4. For post creation: single `PostMgr.save` is atomic (ConcurrentHashMap.put).
5. For follow/unfollow: `computeIfAbsent` + synchronized block on the user's follow set if strict consistency needed.

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
    service/
        UserService.java
        PostService.java
        FollowingService.java
        FeedService.java
        IdGenerator.java
    exception/
        UserNotFoundException.java
        PostDoesNotExistException.java
```
