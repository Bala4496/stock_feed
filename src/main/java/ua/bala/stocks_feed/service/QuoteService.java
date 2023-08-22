package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;
import ua.bala.stocks_feed.repository.QuoteRepository;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final CompanyService companyService;

    @Transactional(readOnly = true)
    public Mono<Quote> getQuoteByCode(String stockCode) {
        return companyService.getCompanyByCode(stockCode)
                .flatMap(company -> quoteRepository.findFirstByCompanyIdOrderByCreatedAtDesc(company.getId()));
    }

    @Transactional(readOnly = true)
    public Mono<Quote> findByCompanyId(Long companyId) {
        return quoteRepository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public Mono<Quote> save(Quote quote) {
        return quoteRepository.save(quote)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "Quote wasn't created")));
    }

    public Mono<Long> getCount() {
        return quoteRepository.count();
    }
}
