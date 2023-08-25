package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;

    public Mono<Quote> getQuoteByCode(String stockCode) {
        return quoteRepository.findFirstByCompanyCodeOrderByCreatedAtDesc(stockCode);
    }

    public Mono<Quote> save(Quote quote) {
        return quoteRepository.save(quote);
    }

    public Mono<Long> getCount() {
        return quoteRepository.count();
    }
}
