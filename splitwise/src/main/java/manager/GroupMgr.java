package manager;

import entity.Group;

import java.util.HashMap;
import java.util.Map;

public class GroupMgr {

    private final Map<String, Group> groups;

    public GroupMgr() {
        this.groups = new HashMap<>();
    }

    public Group save(Group group) {
        groups.put(group.getId(), group);
        return group;
    }
    public Group findById(String id) {
        return groups.get(id);
    }
}
