package ua.bala.stocks_feed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.Company;

public interface CompanyRepository extends ReactiveCrudRepository<Company, Long> {

    Mono<Company> findByCode(String code);
}
