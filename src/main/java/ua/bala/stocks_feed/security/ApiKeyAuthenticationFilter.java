package ua.bala.stocks_feed.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.bala.stocks_feed.service.AuthenticationService;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("ApiKeyAuthenticationFilter start");

        String apiKey = request.getHeader("x-api-apiKey");
        if (!StringUtils.hasText(apiKey)) {
            log.info("Request haven't header \"x-api-apiKey\"");
        } else {
            log.info("Processing authentication for apiKey %s".formatted(apiKey));
            if (authenticationService.isValidApiKey(apiKey)) {
                var user = userDetailsService.loadUserByUsername(apiKey);
                var auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("User %s authenticated".formatted(user.getUsername()));
            }
        }

        log.info("ApiKeyAuthenticationFilter finish");
        filterChain.doFilter(request, response);
    }
}
