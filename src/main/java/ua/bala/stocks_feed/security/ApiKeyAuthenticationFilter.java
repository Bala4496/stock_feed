package ua.bala.stocks_feed.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiKeyAuthenticationFilter extends AuthenticationWebFilter {

    public ApiKeyAuthenticationFilter(ReactiveAuthenticationManager authenticationManager,
                                      ServerAuthenticationConverter authenticationConverter) {
        super(authenticationManager);
        setServerAuthenticationConverter(authenticationConverter);
    }

}