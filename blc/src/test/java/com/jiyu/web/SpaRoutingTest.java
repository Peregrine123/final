package com.jiyu.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SpaRoutingTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void root_serves_spa_entry_without_auth() {
        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        assertNotEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertTrue(resp.getBody() != null && resp.getBody().contains("<div id=\"app\"></div>"));
    }

    @Test
    void history_route_serves_spa_entry_without_auth() {
        // Refreshing a SPA history route (e.g. /login) must still render the SPA shell.
        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:" + port + "/login", String.class);
        assertNotEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertTrue(resp.getBody() != null && resp.getBody().contains("<div id=\"app\"></div>"));
    }
}

