package ua.bala.stocks_feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.dto.QuoteDTO;
import ua.bala.stocks_feed.mapper.QuoteMapper;
import ua.bala.stocks_feed.service.QuoteService;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockV1Controller {

    private final QuoteService quoteService;
    private final QuoteMapper quoteMapper;

    @GetMapping("/{stock_code}/quote")
    public Mono<QuoteDTO> getQuoteByCode(@PathVariable("stock_code") String companyCode) {
        return quoteService.getQuoteByCode(companyCode)
                .map(quoteMapper::map);
    }

}
