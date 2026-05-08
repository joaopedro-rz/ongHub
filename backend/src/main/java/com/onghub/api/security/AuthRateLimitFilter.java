package com.onghub.api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final int REQUESTS_PER_MINUTE = 60;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!isAuthHeavyEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = clientKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k ->
            Bucket.builder()
                .addLimit(Bandwidth.builder()
                    .capacity(REQUESTS_PER_MINUTE)
                    .refillGreedy(REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
                    .build())
                .build());

        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"error\":{\"code\":\"RATE_LIMIT\",\"message\":\"Too many requests\"}}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isAuthHeavyEndpoint(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getServletPath();
        return "/api/v1/auth/login".equals(path)
            || "/api/v1/auth/register".equals(path)
            || "/api/v1/auth/password/forgot".equals(path)
            || "/api/v1/auth/password/reset".equals(path)
            || "/api/v1/auth/refresh".equals(path);
    }

    private static String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
