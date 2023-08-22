package ua.bala.stocks_feed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Quote;

public interface QuoteRepository extends ReactiveCrudRepository<Quote, Long> {

    Mono<Quote> findFirstByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
