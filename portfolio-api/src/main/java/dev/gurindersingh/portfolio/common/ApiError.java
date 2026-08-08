package dev.gurindersingh.portfolio.common;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error) {
        return new ApiError(Instant.now(), status, error, Map.of());
    }

    public static ApiError of(int status, String error, Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, fieldErrors);
    }
}
