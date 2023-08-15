package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;

@Slf4j
@Service
@AllArgsConstructor
public class ExchangeService {

    private final ExchangeAdapter exchangeConnector;

    public Flux<Company> getCompanies() {
        log.info("Getting Companies");
        return exchangeConnector.getCompanies();
    }

    public Mono<Quote> getStockByCode(Integer stockCode) {
        log.info("Getting Stock quotes");
        return exchangeConnector.getStockQuotesByCode(stockCode);
    }

}
