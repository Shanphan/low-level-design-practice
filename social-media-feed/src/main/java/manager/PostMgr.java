package manager;

import entity.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostMgr {

    private Map<String, Post> posts;

    public PostMgr() {
        this.posts = new HashMap<>();
    }

    public Post save(Post post) {
        posts.put(post.getId(), post);
        return post;
    }

    public Post findById(String id) {
        return posts.get(id);
    }

    public List<Post> findByPostedBy(String id) {

        return posts.values()
                .stream()
                .filter(p -> p.getPostedBy().equals(id))
                .toList();
    }

    public void delete(String id) {
        posts.remove(id);

    }

    public boolean existsById(String id) {
        return posts.containsKey(id);
    }
}
