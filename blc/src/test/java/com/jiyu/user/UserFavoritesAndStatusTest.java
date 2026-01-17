package com.jiyu.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyu.dao.CategoryDAO;
import com.jiyu.dao.MovieDAO;
import com.jiyu.dao.UserDAO;
import com.jiyu.dao.UserFavoriteDAO;
import com.jiyu.dao.UserMovieStatusDAO;
import com.jiyu.pojo.Category;
import com.jiyu.pojo.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserFavoritesAndStatusTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    Filter shiroFilter;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserDAO userDAO;

    @Autowired
    MovieDAO movieDAO;

    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    UserFavoriteDAO userFavoriteDAO;

    @Autowired
    UserMovieStatusDAO userMovieStatusDAO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(shiroFilter)
                .build();

        userMovieStatusDAO.deleteAll();
        userFavoriteDAO.deleteAll();
        movieDAO.deleteAll();
        categoryDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    void favorites_are_idempotent_and_removable() throws Exception {
        MockHttpSession session = registerAndLogin("alice", "pw");
        Movie movie = saveMovie("m1");

        mockMvc.perform(put("/api/me/favorites/" + movie.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(true))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        mockMvc.perform(put("/api/me/favorites/" + movie.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(true))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertThat(userFavoriteDAO.findAll()).hasSize(1);

        mockMvc.perform(get("/api/me/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(movie.getId()));

        mockMvc.perform(delete("/api/me/favorites/" + movie.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(false));

        // idempotent delete
        mockMvc.perform(delete("/api/me/favorites/" + movie.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(false));

        assertThat(userFavoriteDAO.findAll()).isEmpty();
    }

    @Test
    void favorites_are_isolated_by_user() throws Exception {
        MockHttpSession a = registerAndLogin("userA", "pw");
        MockHttpSession b = registerAndLogin("userB", "pw");
        Movie movie = saveMovie("m1");

        mockMvc.perform(put("/api/me/favorites/" + movie.getId()).session(a))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(true));

        mockMvc.perform(get("/api/me/favorites").session(b))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // B deleting should not affect A
        mockMvc.perform(delete("/api/me/favorites/" + movie.getId()).session(b))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(false));

        mockMvc.perform(get("/api/me/favorites").session(a))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void watch_status_is_upserted_and_lists_by_status() throws Exception {
        MockHttpSession session = registerAndLogin("alice", "pw");
        Movie movie = saveMovie("m1");

        MvcResult firstWish = mockMvc.perform(
                        put("/api/me/movie-status/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"WISH\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WISH"))
                .andExpect(jsonPath("$.markedAt").isNotEmpty())
                .andReturn();

        JsonNode firstWishJson = objectMapper.readTree(firstWish.getResponse().getContentAsString());
        String markedAt = firstWishJson.get("markedAt").asText();

        // idempotent: same status keeps markedAt
        mockMvc.perform(
                        put("/api/me/movie-status/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"WISH\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WISH"))
                .andExpect(jsonPath("$.markedAt").value(markedAt));

        mockMvc.perform(get("/api/me/movie-status?status=WISH").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(
                        put("/api/me/movie-status/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"WATCHED\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WATCHED"))
                .andExpect(jsonPath("$.markedAt").isNotEmpty());

        mockMvc.perform(get("/api/me/movie-status?status=WISH").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/me/movie-status?status=WATCHED").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private MockHttpSession registerAndLogin(String username, String password) throws Exception {
        Map<String, Object> registerBody = new HashMap<>();
        registerBody.put("username", username);
        registerBody.put("password", password);

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", username);
        loginBody.put("password", password);

        MvcResult loginResult = mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginBody))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        return (MockHttpSession) loginResult.getRequest().getSession(false);
    }

    private Movie saveMovie(String title) {
        Category category = new Category();
        category.setName("Cat");
        Category savedCategory = categoryDAO.save(category);

        Movie movie = new Movie();
        movie.setCategory(savedCategory);
        movie.setTitle(title);
        movie.setCover("cover");
        movie.setCast("cast");
        movie.setDate("2025");
        movie.setPress("press");
        movie.setSummary("summary");
        return movieDAO.save(movie);
    }
}

