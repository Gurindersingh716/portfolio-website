package dev.gurindersingh.portfolio.common;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-IP token bucket held in memory.
 *
 * In-memory is deliberate: this runs as a single instance, and a portfolio
 * contact form does not justify a Redis dependency. If this ever scales
 * horizontally, swap the map for a distributed store — the interface stays the same.
 */
@Component
public class RateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final Duration window;

    public RateLimiter(@Value("${portfolio.rate-limit.capacity:3}") int capacity,
                       @Value("${portfolio.rate-limit.window-minutes:60}") long windowMinutes) {
        this.capacity = capacity;
        this.window = Duration.ofMinutes(windowMinutes);
    }

    public boolean tryConsume(String key) {
        if (key == null || key.isBlank()) {
            return true;
        }
        return buckets.computeIfAbsent(key, k -> newBucket()).tryConsume(1);
    }

    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, window)
                        .build())
                .build();
    }

    public void clear() {
        buckets.clear();
    }
}
