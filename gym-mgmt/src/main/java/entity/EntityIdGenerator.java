package entity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class EntityIdGenerator {

    private static final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    public static String getId(String prefix) {
        AtomicLong counter = counters.computeIfAbsent(prefix, k -> new AtomicLong(0));
        return prefix + counter.incrementAndGet();
    }
}
