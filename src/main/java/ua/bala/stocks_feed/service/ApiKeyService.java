package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.stocks_feed.model.ApiKey;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.repository.ApiKeyRepository;
import ua.bala.stocks_feed.repository.UserRepository;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyService {

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;

    public Mono<ApiKey> createApiKeyInternal(String username) {
        return userRepository.findByUsernameAndEnabledTrue(username)
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
        return userRepository.findByUsernameAndEnabledTrue(username)
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
                .flatMap(key -> Mono.empty());
    }

    public String generateApiKey() {
        return RandomStringUtils.randomAlphabetic(20);
    }

    public Mono<Boolean> isValidApiKey(String apiKey) {
        return apiKeyRepository.findByKeyAndDeletedFalse(apiKey)
                .map(Objects::nonNull);
    }
}
