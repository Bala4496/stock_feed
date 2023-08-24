package ua.bala.stocks_feed.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;
import ua.bala.stocks_feed.repository.QuoteRepositoryRedis;

@Service
@Slf4j
@Primary
@ConditionalOnProperty(name = "redis-cache.enabled", havingValue = "true")
public class QuoteServiceWithRedisCache extends QuoteService {

    private final QuoteRepositoryRedis quoteReactiveRedisTemplate;


    public QuoteServiceWithRedisCache(QuoteRepository quoteRepository,
                                      QuoteRepositoryRedis quoteReactiveRedisTemplate) {
        super(quoteRepository);
        this.quoteReactiveRedisTemplate = quoteReactiveRedisTemplate;
    }

    @Override
    public Mono<Quote> save(Quote quote) {
        return super.save(quote)
                .flatMap(quoteReactiveRedisTemplate::save);
    }

    @Override
    public Mono<Quote> getQuoteByCode(String code) {
        return quoteReactiveRedisTemplate.findByCompanyCode(code)
                .switchIfEmpty(super.getQuoteByCode(code)
                        .flatMap(quoteReactiveRedisTemplate::save)
                );
    }

}