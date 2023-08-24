package ua.bala.stocks_feed.configuration;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    @Value("${rate-limiter.rate}")
    private int limitForPeriod;
    @Value("${rate-limiter.timeline}")
    private long limitRefreshPeriod;
    @Value("${rate-limiter.timeout}")
    private long timeoutDuration;

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig defaultConfig = RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriod))
                .timeoutDuration(Duration.ofMillis(timeoutDuration))
                .build();
        return RateLimiter.of("defaultRateLimiter", defaultConfig);
    }
}