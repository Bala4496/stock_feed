package ua.bala.stocks_feed.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.configuration.WebFluxSecurityConfig;
import ua.bala.stocks_feed.model.ApiKey;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserRole;
import ua.bala.stocks_feed.repository.ApiKeyRepository;
import ua.bala.stocks_feed.repository.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyV1ControllerITTest {

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    ApiKeyRepository apiKeyRepository;

    @Autowired
    UserRepository userRepository;

    static final String API_URL = "/api/v1/companies";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void teatDown() {
        apiKeyRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void receiveCompanies() {
        addKeyForUser(addTestUser());

        given()
                .header("x-api-key", "key")
                .contentType(ContentType.JSON)
                .when()
                .get(API_URL)
                .then()
                .statusCode(200)
                .body("$", hasSize(1000))
                .body("code", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    void reachEndpoint_withInvalidApiKey_Negative() {
        addKeyForUser(addTestUser());

        given()
                .header("x-api-key", "invalidKey")
                .contentType(ContentType.JSON)
                .when()
                .get(API_URL)
                .then()
                .statusCode(401);
    }

    private void addKeyForUser(User user) {
        userRepository.findByUsernameAndEnabledTrue(user.getUsername())
                .map(User::getId)
                .map(userId -> new ApiKey().setUserId(userId).setKey("key"))
                .flatMap(apiKeyRepository::save)
                .block();
    }

    private User addTestUser() {
        User user = new User().setUsername("test").setPassword("{noop}test").setRole(UserRole.ROLE_USER).setEnabled(true);
        return Mono.just(user)
                .flatMap(userRepository::save)
                .block();
    }
}
