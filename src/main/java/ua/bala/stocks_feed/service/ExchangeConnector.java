package ua.bala.stocks_feed.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;

public interface ExchangeConnector {

    Flux<Company> getCompanies();

    Mono<Quote> getStockQuotesByCode(Integer stockCode);
}
