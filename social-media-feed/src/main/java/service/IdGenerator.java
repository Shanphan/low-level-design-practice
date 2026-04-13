package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final Map<String, AtomicInteger> counters  = new ConcurrentHashMap<>();

    public static String createId(String prefix) {

        counters.computeIfAbsent(prefix, k -> new AtomicInteger(0));
        return prefix + "-" + counters.get(prefix).getAndIncrement();

    }
}
