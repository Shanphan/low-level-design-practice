package service;

import entity.Post;
import exception.PostDoesNotExistException;
import exception.UserNotFoundException;
import manager.PostMgr;
import manager.UserMgr;

public class PostService {
    private final PostMgr postMgr;
    private final UserMgr userMgr;

    public PostService(PostMgr postMgr, UserMgr userMgr) {
        this.postMgr = postMgr;
        this.userMgr = userMgr;
    }

    public Post createPost(Post post) {

        if (!userMgr.existById(post.getPostedBy())) {
            throw new UserNotFoundException("USer cannot be found with user id "  + post.getPostedBy());
        }
        return postMgr.save(post);
    }

    public void removePost(String  id) {

        if(!postMgr.existsById(id)) {
            throw new PostDoesNotExistException("The post id does not exist " + id);
        }

        postMgr.delete(id);


    }
}
