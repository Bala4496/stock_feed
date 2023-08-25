package ua.bala.stocks_feed.security;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.ratelimiter.UserRateLimiterManager;
import ua.bala.stocks_feed.service.ApiKeyService;
import ua.bala.stocks_feed.service.UserService;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final String API_KEY_HEADER = "x-api-key";
    private final ApiKeyService apiKeyService;
    private final UserService userService;
    private final UserRateLimiterManager rateLimiterManager;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return Mono.just(rateLimiterManager)
                .flatMap(rateLimiter -> {
                    try {
                        return Mono.justOrEmpty(context.getExchange().getRequest().getHeaders().getFirst(API_KEY_HEADER))
                                .filterWhen(rateLimiter::acquirePermission)
                                .flatMap(this::processAuthorization)
                                .onErrorResume(RequestNotPermitted.class, e ->
                                        Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded")));
                    } catch (RequestNotPermitted e) {
                        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
                    }
                });
    }

    private Mono<AuthorizationDecision> processAuthorization(String apiKey) {
        return Mono.justOrEmpty(apiKey)
                .filterWhen(apiKeyService::isValidApiKey)
                .flatMap(apiKeyService::getByKey)
                .flatMap(key -> userService.getById(key.getUserId()))
                .map(user -> new AuthorizationDecision(true))
                .switchIfEmpty(Mono.just(new AuthorizationDecision(false)));
    }
}
