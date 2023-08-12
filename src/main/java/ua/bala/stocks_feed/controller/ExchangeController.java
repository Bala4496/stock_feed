package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.service.ExchangeService;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
@Slf4j
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
