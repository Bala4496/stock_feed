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
import ua.bala.stocks_feed.configuration.WebFluxSecurityConfig;
import ua.bala.stocks_feed.dto.RegisterUserDTO;
import ua.bala.stocks_feed.repository.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Testcontainers
@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterV1ControllerTest {

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