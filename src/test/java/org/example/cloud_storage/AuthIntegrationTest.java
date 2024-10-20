package org.example.cloud_storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0.11")
            .withExposedPorts(6379);

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Test
    void testUserRegistrationAndLogin() {
        String registerUrl = "/register";
        String loginUrl = "/login";

        String newUser = "{\"username\": \"testuser\", \"password\": \"password123\"}";
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(registerUrl, newUser, String.class);
        assert(registerResponse.getStatusCode()).equals(HttpStatus.OK);

        String loginData = "{\"username\": \"testuser\", \"password\": \"password123\"}";
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginData, String.class);
        assert(loginResponse.getStatusCode()).equals(HttpStatus.OK);
    }
}