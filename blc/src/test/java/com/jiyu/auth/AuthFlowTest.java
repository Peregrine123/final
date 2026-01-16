package com.jiyu.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyu.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    Filter shiroFilter;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserDAO userDAO;

    @BeforeEach
    void cleanDb() {
        // Build MockMvc explicitly (more reliable than relying on auto-config in this project).
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(shiroFilter)
                .build();
        userDAO.deleteAll();
    }

    @Test
    void register_then_login_success() throws Exception {
        String username = "alice";
        String password = "pass123";

        Map<String, Object> registerBody = new HashMap<>();
        registerBody.put("username", username);
        registerBody.put("password", password);

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.username").value(username))
                .andExpect(jsonPath("$.result.role").value("normal"))
                // stored password should be hashed, not plain text
                .andExpect(jsonPath("$.result.password").value(not(password)))
                .andExpect(jsonPath("$.result.salt").isNotEmpty());

        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", username);
        loginBody.put("password", password);

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").value(username));
    }

    @Test
    void register_duplicate_username_fails() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "bob");
        body.put("password", "p1");

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名已被使用"));
    }

    @Test
    void login_wrong_password_fails() throws Exception {
        Map<String, Object> registerBody = new HashMap<>();
        registerBody.put("username", "charlie");
        registerBody.put("password", "right");

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", "charlie");
        loginBody.put("password", "wrong");

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("账号或密码错误"));
    }

    @Test
    void login_unknown_user_fails_with_business_error_not_500() throws Exception {
        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", "nobody");
        loginBody.put("password", "whatever");

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("账号或密码错误"));
    }
}
