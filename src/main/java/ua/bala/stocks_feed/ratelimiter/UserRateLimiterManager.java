package ua.bala.stocks_feed.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRateLimiterManager {

    private final Map<String, RateLimiter> userRateLimiters = new ConcurrentHashMap<>();
    private final RateLimiter defaultRateLimiter;

    public Mono<Boolean> acquirePermission(String apiKey) {
        log.info("Acquire permission for {}", apiKey);
        RateLimiter userRateLimiter = userRateLimiters.computeIfAbsent(apiKey, this::createRateLimiter);
        return Mono.fromCallable(userRateLimiter::acquirePermission);
    }

    private RateLimiter createRateLimiter(String apiKey) {
        return RateLimiter.of(apiKey, defaultRateLimiter::getRateLimiterConfig);
    }
}
