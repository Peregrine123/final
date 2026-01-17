package com.jiyu.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyu.dao.CategoryDAO;
import com.jiyu.dao.MovieCommentDAO;
import com.jiyu.dao.MovieCommentLikeDAO;
import com.jiyu.dao.MovieDAO;
import com.jiyu.dao.UserDAO;
import com.jiyu.dao.UserMovieStatusDAO;
import com.jiyu.pojo.Category;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.User;
import org.junit.jupiter.api.AfterEach;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieCommentsTest {

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
    MovieCommentDAO movieCommentDAO;

    @Autowired
    MovieCommentLikeDAO movieCommentLikeDAO;

    @Autowired
    UserMovieStatusDAO userMovieStatusDAO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(shiroFilter)
                .build();

        userMovieStatusDAO.deleteAll();
        movieCommentLikeDAO.deleteAll();
        movieCommentDAO.deleteAll();
        movieDAO.deleteAll();
        categoryDAO.deleteAll();
        userDAO.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Keep the in-memory DB clean for other test classes that don't know about movie_comment yet.
        userMovieStatusDAO.deleteAll();
        movieCommentLikeDAO.deleteAll();
        movieCommentDAO.deleteAll();
        movieDAO.deleteAll();
        categoryDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    void comment_endpoints_require_login() throws Exception {
        Movie movie = saveMovie("m1");

        mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":5,\"content\":\"nice\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/movies/" + movie.getId() + "/comments"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void submit_comment_is_visible_immediately_and_marks_watched() throws Exception {
        MockHttpSession session = registerAndLogin("alice", "pw");
        Movie movie = saveMovie("m1");

        mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":5,\"content\":\"nice\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value(movie.getId()))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.content").value("nice"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        // linked rule: submitting short review/rating should mark as WATCHED.
        mockMvc.perform(get("/api/me/movie-status/" + movie.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WATCHED"))
                .andExpect(jsonPath("$.markedAt").isNotEmpty());

        // author can see their own comment via /me endpoint.
        mockMvc.perform(get("/api/me/movie-comments/" + movie.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        // list shows immediately (no-audit mode).
        mockMvc.perform(get("/api/movies/" + movie.getId() + "/comments?page=1&size=10").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].content").value("nice"))
                .andExpect(jsonPath("$.items[0].rating").value(5))
                .andExpect(jsonPath("$.items[0].likeCount").value(0))
                .andExpect(jsonPath("$.items[0].likedByMe").value(false))
                .andExpect(jsonPath("$.total").value(1));

        mockMvc.perform(get("/api/movies/" + movie.getId() + "/rating-summary").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.average").value(5.0));
    }

    @Test
    void comment_is_visible_to_other_users_and_likes_are_idempotent() throws Exception {
        MockHttpSession alice = registerAndLogin("alice", "pw");
        Movie movie = saveMovie("m1");

        MvcResult created = mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .session(alice)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":4,\"content\":\"good\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andReturn();

        long commentId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        MockHttpSession bob = registerAndLogin("bob", "pw");

        // visible to other users
        mockMvc.perform(get("/api/movies/" + movie.getId() + "/comments?page=1&size=10").session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].content").value("good"))
                .andExpect(jsonPath("$.items[0].rating").value(4))
                .andExpect(jsonPath("$.items[0].likeCount").value(0))
                .andExpect(jsonPath("$.items[0].likedByMe").value(false));

        // like (idempotent)
        mockMvc.perform(put("/api/me/movie-comment-likes/" + commentId).session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));

        mockMvc.perform(put("/api/me/movie-comment-likes/" + commentId).session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));

        mockMvc.perform(get("/api/movies/" + movie.getId() + "/comments?page=1&size=10").session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].likeCount").value(1))
                .andExpect(jsonPath("$.items[0].likedByMe").value(true));

        // another user sees count but not likedByMe
        mockMvc.perform(get("/api/movies/" + movie.getId() + "/comments?page=1&size=10").session(alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].likeCount").value(1))
                .andExpect(jsonPath("$.items[0].likedByMe").value(false));

        // unlike (idempotent)
        mockMvc.perform(delete("/api/me/movie-comment-likes/" + commentId).session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));

        mockMvc.perform(delete("/api/me/movie-comment-likes/" + commentId).session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));

        mockMvc.perform(get("/api/movies/" + movie.getId() + "/rating-summary").session(bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.average").value(4.0));
    }

    @Test
    void xss_tags_are_stripped_and_overlong_content_is_rejected() throws Exception {
        MockHttpSession session = registerAndLogin("alice", "pw");
        Movie movie = saveMovie("m1");

        mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":5,\"content\":\"<script>alert(1)</script> hello\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(not("<script>alert(1)</script> hello")))
                .andExpect(jsonPath("$.content").value("alert(1) hello"));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append('a');
        }

        mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":5,\"content\":\"" + sb + "\"}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void rate_limit_blocks_spam_updates() throws Exception {
        MockHttpSession session = registerAndLogin("alice", "pw");
        Movie movie = saveMovie("m1");

        mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":5,\"content\":\"v1\"}")
                )
                .andExpect(status().isOk());

        // immediately update: should hit rate limit (default 10s).
        mockMvc.perform(
                        put("/api/me/movie-comments/" + movie.getId())
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"rating\":5,\"content\":\"v2\"}")
                )
                .andExpect(status().isTooManyRequests());
    }

    private MockHttpSession registerAndLogin(String username, String password) throws Exception {
        registerOnly(username, password);
        return loginOnly(username, password);
    }

    private void registerOnly(String username, String password) throws Exception {
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
                .andExpect(jsonPath("$.result.password").value(not(password)));
    }

    private MockHttpSession loginOnly(String username, String password) throws Exception {
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
