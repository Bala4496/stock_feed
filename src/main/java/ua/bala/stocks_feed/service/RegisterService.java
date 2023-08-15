package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserRole;
import ua.bala.stocks_feed.repository.UserRepository;

import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
        if (Objects.isNull(user.getRole())) {
            user.setRole(UserRole.ROLE_USER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user)
                .doOnSuccess(usr -> log.info("User '%s' registered".formatted(usr.getUsername())))
                .doOnError(err -> log.info("User not registered", err));
    }

}
