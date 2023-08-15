package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;

import static ua.bala.stocks_feed.configuration.CacheConfig.COMPANY_CACHE_KEY;
import static ua.bala.stocks_feed.configuration.CacheConfig.QUOTE_CACHE_KEY;

@Slf4j
@Service
@AllArgsConstructor
public class ExchangeService {

    private final ExchangeAdapter exchangeAdapter;

    @Cacheable(COMPANY_CACHE_KEY)
    public Flux<Company> getCompanies() {
        log.info("Getting Companies");
        return exchangeAdapter.getAccessToken()
                .flatMapMany(exchangeAdapter::getCompanies)
                .cache();
    }

    @Cacheable(QUOTE_CACHE_KEY)
    public Mono<Quote> getStockByCode(Integer stockCode) {
        log.info("Getting Stock quotes");
        return exchangeAdapter.getAccessToken()
                .flatMap(token -> exchangeAdapter.getStockQuotesByCode(token, stockCode))
                .cache();
    }

}
