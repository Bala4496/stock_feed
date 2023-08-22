package ua.bala.stocks_feed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.ApiKey;

public interface ApiKeyRepository extends ReactiveCrudRepository<ApiKey, Long> {

    Mono<ApiKey> findByKeyAndDeletedFalse(String key);

    Mono<ApiKey> findByUserIdAndDeletedFalse(Long userId);

}
