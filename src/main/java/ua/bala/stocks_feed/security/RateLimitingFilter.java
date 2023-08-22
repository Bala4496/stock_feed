package ua.bala.stocks_feed.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.exception.RateLimitExceededException;
import ua.bala.stocks_feed.service.RateLimiterService;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitingFilter implements WebFilter {

    private final RateLimiterService rateLimiterService;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        log.info("RateLimiter invoked");
        if (!rateLimiterService.isAllowed()) {
            log.info("RateLimiter block request");
            throw new RateLimitExceededException();
        }
        return chain.filter(exchange);
    }
}
