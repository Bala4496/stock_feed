package ua.bala.stocks_feed.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.bala.stocks_feed.exception.InvalidUsernameException;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.repository.UserRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        validateUser(user);
        log.info("User %s validated".formatted(user.getUsername()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (Objects.isNull(userRepository.save(user).getId())) {
            log.error("User %s wasn't save".formatted(user.getUsername()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User %s wasn't created".formatted(user.getUsername()));
        }
        log.info("User %s saved".formatted(user.getUsername()));
    }

    private void validateUser(User user) {
        String username = user.getUsername();
        if (userRepository.existsUserByUsername(username)) {
            log.error("User %s already exist".formatted(user.getUsername()));
            throw new InvalidUsernameException("User with username %s already exists".formatted(username));
        }
    }

}
