package ua.bala.stocks_feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsernameAndEnabledTrue(username)
                .cast(UserDetails.class);
    }

    public Mono<User> getById(Long id) {
        return userRepository.findByIdAndEnabledTrue(id);
    }
}
