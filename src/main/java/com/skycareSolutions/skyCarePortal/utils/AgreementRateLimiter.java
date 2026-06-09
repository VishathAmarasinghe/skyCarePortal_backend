package com.skycareSolutions.skyCarePortal.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgreementRateLimiter {

    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();

    @Value("${agreement.public.rate-limit-per-minute:30}")
    private int limitPerMinute;

    public void checkOrThrow(String key) {
        long now = Instant.now().toEpochMilli();
        long windowStart = now - 60_000L;
        List<Long> timestamps = requestLog.computeIfAbsent(key, k -> new ArrayList<>());
        synchronized (timestamps) {
            timestamps.removeIf(ts -> ts < windowStart);
            if (timestamps.size() >= limitPerMinute) {
                throw new RuntimeException("Too many requests. Please try again in a minute.");
            }
            timestamps.add(now);
        }
    }
}
