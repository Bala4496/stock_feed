package ua.bala.stocks_feed.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.service.ExchangeService;

@Slf4j
@RestController
@RequestMapping("/api/v1/exchange")
@AllArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/companies")
    public Flux<Company> receiveCompanies() {
        log.info("Receiving companies");
        Flux<Company> companies = exchangeService.getCompanies();
        log.info("Received companies");
        return companies;
    }

    @GetMapping("/stocks/{stock_code}/quote")
    public Mono<Quote> getStockByCode(@PathVariable("stock_code") Integer stockCode) {
        log.info("Receiving stock for code : %d".formatted(stockCode));
        return exchangeService.getStockByCode(stockCode)
                .doOnNext(quote -> log.info("Received stock: {}", quote));
    }

}
