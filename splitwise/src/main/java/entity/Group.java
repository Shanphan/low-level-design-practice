package entity;

import Service.IdGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Group {

    private String id;
    private String name;
    private final Set<String> memberIds;

    public Group(String name) {
        this.id = IdGenerator.createId("GROUP");
        this.name = name;
        this.memberIds = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public Set<String> getMemberId() {
        return memberIds;
    }
}
