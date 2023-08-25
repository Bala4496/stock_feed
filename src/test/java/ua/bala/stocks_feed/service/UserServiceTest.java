package ua.bala.stocks_feed.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserRole;
import ua.bala.stocks_feed.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @MockBean
    UserRepository userRepository;
    @Autowired
    UserService userService;


    @Test
    void findByUsername() {
        User user = getTestUser();
        String username = user.getUsername();

        when(userRepository.findByUsernameAndEnabledTrue(username)).thenReturn(Mono.just(user));

        UserDetails foundUser = userService.findByUsername(username).block();

        assertNotNull(foundUser);
        assertEquals("test", foundUser.getUsername());
        assertEquals("test", foundUser.getPassword());
        assertTrue(foundUser.isEnabled());
    }

    @Test
    void getById() {
        User user = getTestUser();
        Long userId = user.getId();

        when(userRepository.findByIdAndEnabledTrue(userId)).thenReturn(Mono.just(user));

        User foundUser = userService.getById(userId).block();

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("test", foundUser.getUsername());
        assertEquals("test", foundUser.getPassword());
        assertEquals(UserRole.ROLE_USER, foundUser.getRole());
        assertTrue(foundUser.isEnabled());
    }

    private User getTestUser() {
        return new User()
                .setId(1L)
                .setUsername("test")
                .setPassword("test")
                .setRole(UserRole.ROLE_USER)
                .setEnabled(true);
    }
}