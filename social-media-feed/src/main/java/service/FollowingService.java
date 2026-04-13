package service;

import entity.User;
import exception.UserNotFoundException;
import manager.FollowMgr;
import manager.UserMgr;

public class FollowingService {

    private final UserMgr userMgr;
    private final FollowMgr followMgr;

    public FollowingService(UserMgr userMgr, FollowMgr followMgr) {
        this.userMgr = userMgr;
        this.followMgr = followMgr;
    }

    public void follow(String userId, String followId) {


        if(userId.equals(followId)) {
            throw new IllegalArgumentException("User " + userId + "cannot follow self " + followId);
        }

        boolean userExists = userMgr.existById(userId);
        boolean followingExists = userMgr.existById(followId);

        if(!userExists) {
            throw new UserNotFoundException("USer cannot be found with user id "  + userId);
        }

        if(!followingExists) {
            throw new UserNotFoundException("USer cannot be found with user id "  + followId);
        }



        followMgr.save(userId, followId);

    }

    public void unFollow(String userId, String followId) {

        boolean userExists = userMgr.existById(userId);
        boolean followingExists = userMgr.existById(followId);

        if(!userExists) {
            throw new UserNotFoundException("USer cannot be found with user id "  + userId);
        }

        if(!followingExists) {
            throw new UserNotFoundException("USer cannot be found with user id "  + followId);
        }

        followMgr.delete(userId, followId);

    }
}
