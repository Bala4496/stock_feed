package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    //    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    public Mono<String> generateApiKey(User user) {
        String username = user.getUsername();
        log.info("Generating ApiKey for %s".formatted(username));
        if (userRepository.findByUsername(username).isEmpty()) {
            throw new AuthenticationServiceException("User with name %s not exist".formatted(username));
        }

//        String key = String.valueOf(username.hashCode());
//        redisTemplate.opsForValue().set(key, user.getUsername(), 6400);
//        return key;
        log.info("Generated ApiKey for %s".formatted(username));
        return Mono.just(username);
    }

    public boolean isValidApiKey(String apiKey) {
//          String apiKey = (String) redisTemplate.opsForValue().get(apiKey);
        if (userRepository.findByUsername(apiKey).isEmpty()) {
            throw new AuthenticationServiceException("APIKey is not valid");
        }
        return true;
    }

}
