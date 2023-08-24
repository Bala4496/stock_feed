package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.stocks_feed.model.ApiKey;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.repository.ApiKeyRepository;
import ua.bala.stocks_feed.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyService {

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final LockProvider lockProvider;
    private final LockConfiguration lockConfig;

    public Mono<ApiKey> createApiKey(String username) {
        return Mono.deferContextual(context -> {
            Optional<SimpleLock> lockOptional = lockProvider.lock(lockConfig);
            if (lockOptional.isPresent()) {
                SimpleLock lock = lockOptional.get();
                try {
                    return createApiKeyInternal(username);
                } finally {
                    lock.unlock();
                }
            } else {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ApiKey wasn't created"));
            }
        });
    }

    private Mono<ApiKey> createApiKeyInternal(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    Mono<Void> deleteAndSave = apiKeyRepository.findByUserIdAndDeletedFalse(user.getId())
                            .flatMap(apiKey -> {
                                apiKey.setDeleted(true);
                                return apiKeyRepository.save(apiKey);
                            })
                            .then();

                    return deleteAndSave.then(Mono.defer(() -> {
                        ApiKey newApiKey = new ApiKey();
                        newApiKey.setUserId(user.getId());
                        newApiKey.setKey(generateApiKey());
                        return apiKeyRepository.save(newApiKey);
                    }));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ApiKey> getApiKeyByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .flatMap(apiKeyRepository::findByUserIdAndDeletedFalse);
    }

    public Mono<ApiKey> getByKey(String key) {
        return apiKeyRepository.findByKeyAndDeletedFalse(key);
    }

    public Mono<Void> deleteApiKey(String apiKey) {
        return apiKeyRepository.findByKeyAndDeletedFalse(apiKey)
                .doOnNext(key -> key.setDeleted(true))
                .flatMap(apiKeyRepository::save)
                .cast(Void.class);
    }

    public String generateApiKey() {
        return RandomStringUtils.randomAlphabetic(20);
    }

    public Mono<Boolean> isValidApiKey(String apiKey) {
        return apiKeyRepository.findByKeyAndDeletedFalse(apiKey)
                .map(Objects::nonNull);
    }
}
