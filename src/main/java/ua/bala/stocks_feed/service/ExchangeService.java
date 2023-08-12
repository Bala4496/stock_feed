package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;

@Service
@AllArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeConnector exchangeConnector;

    public Flux<Company> getCompanies() {
        return exchangeConnector.getCompanies();
    }

    public Mono<Quote> getStockByCode(Integer stockCode) {
        return exchangeConnector.getStockQuotesByCode(stockCode);
    }

}
