package ua.bala.stocks_feed.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import ua.bala.stocks_feed.configuration.WebFluxSecurityConfig;
import ua.bala.stocks_feed.model.ApiKey;
import ua.bala.stocks_feed.model.Company;
import ua.bala.stocks_feed.model.User;
import ua.bala.stocks_feed.model.UserRole;
import ua.bala.stocks_feed.repository.ApiKeyRepository;
import ua.bala.stocks_feed.repository.CompanyRepository;
import ua.bala.stocks_feed.repository.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@ActiveProfiles("test")
@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyV1ControllerITTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://%s:%d/%s".formatted(
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName())
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @BeforeAll
    static void startContainers() {
        postgres.start();
    }

    @AfterAll
    static void stopContainers() {
        postgres.stop();
    }

    @Autowired
    ApiKeyRepository apiKeyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    static final String API_URL = "/api/v1/companies";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        addTestCompany();
    }

    @AfterEach
    void teatDown() {
        companyRepository.deleteAll().block();
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
                .body("$", hasSize(1))
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

    private Company addTestCompany() {
        Company company = new Company().setName("Company").setCode("comp");
        return Mono.just(company)
                .flatMap(companyRepository::save)
                .block();
    }
}
