package ua.bala.stocks_feed.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserRole;
import ua.bala.stocks_feed.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;
    @Autowired
    RegisterService registerService;

    @Test
    void registerUser() {
        User inputUser = new User().setUsername("test").setPassword("test");

        when(passwordEncoder.encode("test")).thenReturn("encodedTest");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        User outputUser = registerService.registerUser(inputUser).block();

        assertNotNull(outputUser);
        assertEquals("test", outputUser.getUsername());
        assertEquals("encodedTest", outputUser.getPassword());
        assertEquals(UserRole.ROLE_USER, outputUser.getRole());
        assertTrue(outputUser.isEnabled());
    }
}