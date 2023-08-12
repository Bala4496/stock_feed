package ua.bala.stocks_feed.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.bala.stocks_feed.repository.UserRepository;

import java.util.Collections;

@Service
@Slf4j
public class StockFeedUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Receiving UserDetails for user %s".formatted(username));

        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User details not found for the user key : %s".formatted(username));
        }

        return new User(
                user.get().getUsername(),
                user.get().getPassword(),
                Collections.emptyList());
    }
}
