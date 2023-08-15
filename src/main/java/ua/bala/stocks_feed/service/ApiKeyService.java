package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.exception.InvalidApiKeyException;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserKey;
import ua.bala.stocks_feed.repository.UserKeyRepository;
import ua.bala.stocks_feed.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class ApiKeyService {

    private final UserRepository userRepository;
    private final UserKeyRepository userKeyRepository;

    public Mono<String> getApiKey(User usr) {
        String username = usr.getUsername();
        log.info("Generating ApiKey for %s".formatted(username));
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with username '%s' not exist".formatted(username))))
                .flatMap(user -> userKeyRepository.findUserKeyByUserId(user.getId())
                        .filter(userKey -> userKey.getExpiredAt().isAfter(LocalDateTime.now()))
                        .next()
                        .map(UserKey::getKey)
                        .switchIfEmpty(generateAndSaveApiKey(user).map(UserKey::getKey))
                )
                .doOnSuccess(key -> log.info("Generated ApiKey for '%s'".formatted(username)))
                .doOnError(throwable -> log.error("ApiKey wasn't generated  for '%s'".formatted(username), throwable));
    }

    private Mono<UserKey> generateAndSaveApiKey(User user) {
        return Mono.fromCallable(() -> RandomStringUtils.randomAlphabetic(20))
                .doOnNext(key -> log.info("Generated ApiKey for %s".formatted(user.getUsername())))
                .flatMap(key -> userKeyRepository.save(new UserKey()
                        .setKey(key)
                        .setUserId(user.getId())
                        .setExpiredAt(LocalDateTime.now().plusHours(1))
                ))
                .doOnSuccess(key -> log.info("Generated ApiKey saved"))
                .doOnError(err -> log.info("Generated ApiKey not saved", err));
    }

    public Mono<Boolean> isValidApiKey(String apiKey) {
        log.info("Starting apiKey '{}' validation", apiKey);
        return getUserByApiKey(apiKey)
                .doOnNext(user -> log.info(user.toString()))
                .map(User::isEnabled)
                .switchIfEmpty(Mono.error(new InvalidApiKeyException("User key expired")))
                .doOnSuccess(key -> log.info("ApiKey is valid"));
    }

    public Mono<User> getUserByApiKey(String apiKey) {
        return userKeyRepository.findUserKeyByKey(apiKey)
                .filter(userKey -> userKey.getExpiredAt().isAfter(LocalDateTime.now()))
                .flatMap(userKey -> userRepository.findById(userKey.getUserId()));
    }

}
