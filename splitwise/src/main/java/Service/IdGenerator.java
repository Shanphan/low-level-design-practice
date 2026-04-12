package Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final Map<String, AtomicLong> counters = new HashMap<>();

    public static String createId(String prefix) {
        counters.putIfAbsent(prefix, new AtomicLong(0));
        return prefix + "_" + counters.get(prefix).incrementAndGet();
    }
}
