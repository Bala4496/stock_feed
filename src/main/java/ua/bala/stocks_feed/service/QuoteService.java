package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;

    @Transactional(readOnly = true)
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
