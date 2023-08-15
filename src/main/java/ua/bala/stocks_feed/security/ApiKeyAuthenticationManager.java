package ua.bala.stocks_feed.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.exception.InvalidApiKeyException;
import ua.bala.stocks_feed.service.ApiKeyService;

@Slf4j
@Component
@AllArgsConstructor
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private ApiKeyService apiKeyService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("Start authentication");
        return Mono.justOrEmpty(authentication)
                .cast(ApiKeyToken.class)
                .filterWhen(token -> apiKeyService.isValidApiKey(token.getCredentials()))
                .flatMap(token -> apiKeyService.getUserByApiKey(token.getCredentials()))
                .map(user -> new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()))
                .cast(Authentication.class)
                .switchIfEmpty(Mono.error(new InvalidApiKeyException("Invalid ApiKey")));
    }
}
