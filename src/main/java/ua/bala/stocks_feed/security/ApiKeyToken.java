package ua.bala.stocks_feed.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class ApiKeyToken extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyToken(String apiKey) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.apiKey = apiKey;
    }

    @Override
    public String getCredentials() {
        return apiKey;
    }

    @Override
    public String getPrincipal() {
        return apiKey;
    }
}
