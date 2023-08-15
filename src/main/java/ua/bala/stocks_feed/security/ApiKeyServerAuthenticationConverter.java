package ua.bala.stocks_feed.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final static String HEADER_NAME = "x-api-key";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HEADER_NAME))
                .map(ApiKeyToken::new)
                .cast(Authentication.class);
    }
}

