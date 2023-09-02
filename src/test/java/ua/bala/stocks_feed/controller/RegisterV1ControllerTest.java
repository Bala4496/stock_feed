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
import ua.bala.stocks_feed.configuration.WebFluxSecurityConfig;
import ua.bala.stocks_feed.dto.RegisterUserDTO;
import ua.bala.stocks_feed.repository.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Testcontainers
@ActiveProfiles("test")
@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterV1ControllerTest {

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
    UserRepository userRepository;

    static final String API_URL = "/api/v1/register";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void teatDown() {
        userRepository.deleteAll().block();
    }

    @Test
    void registerUser() {
        given()
                .contentType(ContentType.JSON)
                .body(getTestUser())
                .when()
                .post(API_URL)
                .then()
                .statusCode(200)
                .body("username", notNullValue())
                .body("password", nullValue())
                .body("role", notNullValue());
    }

    @Test
    void registerUser_withInvalidUsername_Negative() {
        given()
                .contentType(ContentType.JSON)
                .body(getTestUser().setUsername(null))
                .when()
                .post(API_URL)
                .then()
                .statusCode(500);
    }

    @Test
    void registerUser_withInvalidPassword_Negative() {
        given()
                .contentType(ContentType.JSON)
                .body(getTestUser().setPassword(null))
                .when()
                .post(API_URL)
                .then()
                .statusCode(500);
    }

    private static RegisterUserDTO getTestUser() {
        return new RegisterUserDTO().setUsername("test").setPassword("test");
    }
}