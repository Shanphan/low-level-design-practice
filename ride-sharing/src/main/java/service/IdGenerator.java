package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    public static String createId(String prefix) {
        counters.putIfAbsent(prefix, new AtomicLong(0));
        return prefix + "_" + counters.get(prefix).incrementAndGet();
    }
}
