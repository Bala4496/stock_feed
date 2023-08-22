package ua.bala.stocks_feed.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.exception.InvalidApiKeyException;
import ua.bala.stocks_feed.service.ApiKeyService;
import ua.bala.stocks_feed.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    private final ApiKeyService apiKeyService;
    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("ApiKeyAuthenticationManager invoked {}", authentication);
        return Mono.justOrEmpty(authentication)
                .cast(ApiKeyToken.class)
                .filterWhen(token -> apiKeyService.isValidApiKey(token.getCredentials()))
                .switchIfEmpty(Mono.error(new InvalidApiKeyException()))
                .flatMap(token -> apiKeyService.getApiKeyByKey(token.getCredentials()))
                .flatMap(key -> userService.getEnabledById(key.getUserId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not enabled")))
                .map(user -> new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()))
                .cast(Authentication.class);
    }
}
