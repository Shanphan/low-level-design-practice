package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    public static String createId(String prefix) {
        counters.computeIfAbsent(prefix, k -> new AtomicLong(0));
        return prefix + "_" + counters.get(prefix).incrementAndGet();
    }
}
