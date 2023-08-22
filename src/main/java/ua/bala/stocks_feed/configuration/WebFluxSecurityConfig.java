package ua.bala.stocks_feed.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.security.ApiKeyAuthenticationManager;
import ua.bala.stocks_feed.security.ApiKeyToken;
import ua.bala.stocks_feed.security.RateLimitingFilter;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebFluxSecurityConfig {

    private final static String API_KEY_HEADER = "x-api-key";
    private final ApiKeyAuthenticationManager apiKeyAuthenticationManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            RateLimitingFilter rateLimitingFilter) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(spec -> spec
                        .pathMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
                        .pathMatchers("/api/v1/api-key").permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(rateLimitingFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .addFilterAt(apiKeyAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(getExceptionHandlingCustomizer()
                )
                .build();
    }

    @Bean
    public AuthenticationWebFilter apiKeyAuthenticationFilter() {
        log.info("apiKeyAuthenticationFilter invoked");
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(apiKeyAuthenticationManager);
        authenticationFilter.setRequiresAuthenticationMatcher(apiKeyAuthenticationMatcher());
        authenticationFilter.setServerAuthenticationConverter(getAuthenticationConverter());
        return authenticationFilter;
    }

    private ServerWebExchangeMatcher apiKeyAuthenticationMatcher() {
        log.info("apiKeyAuthenticationMatcher invoked");
        return exchange -> exchange.getRequest().getHeaders().containsKey(API_KEY_HEADER) ?
                ServerWebExchangeMatcher.MatchResult.match() :
                ServerWebExchangeMatcher.MatchResult.notMatch();
    }

    private ServerAuthenticationConverter getAuthenticationConverter() {
        log.info("getAuthenticationConverter invoked");
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER))
                .map(ApiKeyToken::new)
                .cast(Authentication.class);
    }

    private static Customizer<ServerHttpSecurity.ExceptionHandlingSpec> getExceptionHandlingCustomizer() {
        return spec -> spec
                .authenticationEntryPoint((exchange, exception) -> {
                    log.error("Error while authorization : {}", exception.getMessage(), exception);
                    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                })
                .accessDeniedHandler((exchange, exception) -> {
                    log.error("Error while getting access to resource : {}", exception.getMessage(), exception);
                    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                });
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
