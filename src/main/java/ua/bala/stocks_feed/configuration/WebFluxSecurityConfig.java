package ua.bala.stocks_feed.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.security.ApiKeyAuthorizationManager;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebFluxSecurityConfig {

    private final ApiKeyAuthorizationManager apiKeyAuthorizationManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(spec -> spec
                        .pathMatchers(HttpMethod.GET, "/liveness", "/readiness").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
                        .pathMatchers("/api/v1/api-key/**").authenticated()
                        .anyExchange().access(apiKeyAuthorizationManager)
                )
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint((exchange, exception) -> {
                            log.error("Error while authorization : {}", exception.getMessage(), exception);
                            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                        })
                        .accessDeniedHandler((exchange, exception) -> {
                            log.error("Error while getting access to resource : {}", exception.getMessage(), exception);
                            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                        }))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
