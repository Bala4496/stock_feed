package ua.bala.stocks_feed.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.UserKey;

public interface UserKeyRepository extends ReactiveCrudRepository<UserKey, Long> {

    Mono<UserKey> findUserKeyByKey(String key);

    Flux<UserKey> findUserKeyByUserId(Long userId);
}
