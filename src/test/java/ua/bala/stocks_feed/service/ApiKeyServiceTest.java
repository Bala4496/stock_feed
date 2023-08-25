package ua.bala.stocks_feed.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.model.ApiKey;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserRole;
import ua.bala.stocks_feed.repository.ApiKeyRepository;
import ua.bala.stocks_feed.repository.UserRepository;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    ApiKeyRepository apiKeyRepository;
    @Autowired
    ApiKeyService apiKeyService;

    @Test
    void createApiKeyInternal() {
        User user = getTestUser();
        String username = user.getUsername();

        when(userRepository.findByUsernameAndEnabledTrue(username)).thenReturn(Mono.just(getTestUser()));
        when(apiKeyRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Mono.empty());
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(Mono.just(getApiKey()));

        ApiKey apiKey = apiKeyService.createApiKeyInternal(username).block();

        assertTrue(Objects.nonNull(apiKey));
    }

    @Test
    void getApiKeyByUsername() {
        User user = getTestUser();
        String username = user.getUsername();

        when(userRepository.findByUsernameAndEnabledTrue(username)).thenReturn(Mono.just(getTestUser()));
        when(apiKeyRepository.findByUserIdAndDeletedFalse(user.getId())).thenReturn(Mono.just(getApiKey()));

        ApiKey apiKey = apiKeyService.getApiKeyByUsername(username).block();

        assertTrue(Objects.nonNull(apiKey));
    }

    @Test
    void getByKey() {
        String key = getApiKey().getKey();

        when(apiKeyRepository.findByKeyAndDeletedFalse(key)).thenReturn(Mono.just(getApiKey()));

        ApiKey apiKey = apiKeyService.getByKey(key).block();

        assertTrue(Objects.nonNull(apiKey));
    }

    @Test
    void deleteApiKey() {
        String key = getApiKey().getKey();

        when(apiKeyRepository.findByKeyAndDeletedFalse(key)).thenReturn(Mono.just(getApiKey()));
        when(apiKeyRepository.save(getApiKey())).thenReturn(Mono.just(getApiKey()));

        assertDoesNotThrow(() -> apiKeyService.deleteApiKey(key));
    }

    @Test
    void generateApiKey() {
        String key = apiKeyService.generateApiKey();

        assertEquals(20, key.length());
    }

    @Test
    void isValidApiKey() {
        String key = getApiKey().getKey();

        when(apiKeyRepository.findByKeyAndDeletedFalse(key)).thenReturn(Mono.just(getApiKey()));

        assertEquals(Boolean.TRUE, apiKeyService.isValidApiKey(key).block());
    }


    @Test
    void isValidApiKey_Negative() {
        String key = getApiKey().getKey();

        when(apiKeyRepository.findByKeyAndDeletedFalse(key)).thenReturn(Mono.empty());

        assertNull(apiKeyService.isValidApiKey(key).block());
    }

    private User getTestUser() {
        return new User()
                .setId(1L)
                .setUsername("test")
                .setPassword("test")
                .setRole(UserRole.ROLE_USER)
                .setEnabled(true);
    }

    private ApiKey getApiKey() {
        return new ApiKey()
                .setId(1L)
                .setKey("key")
                .setUserId(1L)
                .setDeleted(false);
    }
}