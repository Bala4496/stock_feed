package ua.bala.stocks_feed.repository;

import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;

import static ua.bala.stocks_feed.configuration.RedisConfiguration.QUOTE_KEY;

@Repository
public class QuoteRepositoryRedis {

    private final ReactiveHashOperations<String, Long, Quote> quoteReactiveRedisOperations;

    public QuoteRepositoryRedis(ReactiveRedisOperations<String, Quote> quoteReactiveRedisOperations) {
        this.quoteReactiveRedisOperations = quoteReactiveRedisOperations.opsForHash();
    }

    public Mono<Quote> findByCompanyId(Long companyId) {
        return quoteReactiveRedisOperations.get(QUOTE_KEY, companyId);
    }

    public Flux<Quote> findAll() {
        return quoteReactiveRedisOperations.values(QUOTE_KEY);
    }

    public Mono<Quote> save(Quote quote) {
        return quoteReactiveRedisOperations.put(QUOTE_KEY, quote.getCompanyId(), quote)
                .thenReturn(quote);
    }

    public Mono<Boolean> deleteByCompanyId(Long companyId) {
        return quoteReactiveRedisOperations.remove(QUOTE_KEY, companyId).hasElement();
    }
}



