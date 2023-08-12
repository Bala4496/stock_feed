package ua.bala.stocks_feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bala.stocks_feed.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsUserByUsername(String username);
}
