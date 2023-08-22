package ua.bala.stocks_feed.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;
import ua.bala.stocks_feed.repository.QuoteRepositoryRedis;

@Service
@Slf4j
@Primary
@ConditionalOnProperty(name = "redis-cache.enabled", havingValue = "true")
public class QuoteServiceWithRedisCache extends QuoteService {

    private final CompanyService companyService;
    private final QuoteRepositoryRedis quoteReactiveRedisTemplate;


    public QuoteServiceWithRedisCache(QuoteRepository quoteRepository,
                                      CompanyService companyService,
                                      QuoteRepositoryRedis quoteReactiveRedisTemplate) {
        super(quoteRepository, companyService);
        this.companyService = companyService;
        this.quoteReactiveRedisTemplate = quoteReactiveRedisTemplate;
    }

    @Override
    public Mono<Quote> save(Quote quote) {
        return super.save(quote)
                .flatMap(quoteReactiveRedisTemplate::save);
    }

    @Override
    public Mono<Quote> getQuoteByCode(String code) {
        return companyService.getCompanyByCode(code)
                .map(Company::getId)
                .flatMap(companyId -> quoteReactiveRedisTemplate.findByCompanyId(companyId)
                        .switchIfEmpty(super.findByCompanyId(companyId)
                                .flatMap(quoteReactiveRedisTemplate::save)
                        )
                );
    }

}