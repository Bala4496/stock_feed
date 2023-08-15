package ua.bala.stocks_feed.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String REFRESH_TOKEN_CACHE_KEY = "refreshTokenCache";
    public static final String ACCESS_TOKEN_CACHE_KEY = "accessTokenCache";
    public static final String COMPANY_CACHE_KEY = "companiesCache";
    public static final String QUOTE_CACHE_KEY = "quoteCache";
}
