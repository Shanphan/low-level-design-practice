package entity;

import java.util.UUID;

public class IdGenerator {

    private static String id;

    public static String setId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
