package ua.bala.stocks_feed.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;

import java.util.function.UnaryOperator;

import static ua.bala.stocks_feed.configuration.RedisConfig.QUOTE_KEY_PREFIX;

@Repository
public class QuoteRepositoryRedis {

    private final ReactiveRedisTemplate<String, Quote> quoteReactiveRedisTemplate;
    private final UnaryOperator<String> prefixApplier = (String key) -> String.join("#", QUOTE_KEY_PREFIX, key);

    public QuoteRepositoryRedis(ReactiveRedisTemplate<String, Quote> quoteReactiveRedisTemplate) {
        this.quoteReactiveRedisTemplate = quoteReactiveRedisTemplate;
    }

    public Mono<Quote> findByCompanyCode(String companyCode) {
        return quoteReactiveRedisTemplate.opsForValue().get(companyCode);
    }

    public Mono<Quote> save(Quote quote) {
        return quoteReactiveRedisTemplate.opsForValue().set(prefixApplier.apply(quote.getCompanyCode()), quote)
                .thenReturn(quote);
    }
}



