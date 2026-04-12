package Service;

import entity.Group;
import manager.GroupMgr;

public class GroupService {

    private final GroupMgr groupMgr;

    public GroupService(GroupMgr groupMgr) {
        this.groupMgr = groupMgr;
    }

    public Group addGroup(Group group) {
        return groupMgr.save(group);
    }

    public Group addMember(String groupId, String userId) {
        Group group = groupMgr.findById(groupId);

        if(group == null) {
            throw new GroupNotFoundException("Group cannot be found " + groupId);
        }

        group.getMemberId().add(userId);
        return groupMgr.save(group);;
    }
}
