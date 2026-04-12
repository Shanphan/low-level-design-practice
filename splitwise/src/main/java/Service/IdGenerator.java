package Service;

import java.util.UUID;

public class IdGenerator {

    public static String createId(String prefix) {
        return prefix + UUID.randomUUID();
    }
}
