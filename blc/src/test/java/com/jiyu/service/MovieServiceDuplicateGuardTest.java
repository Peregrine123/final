package com.jiyu.service;

import com.jiyu.dao.MovieDAO;
import com.jiyu.pojo.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class MovieServiceDuplicateGuardTest {

    @Autowired
    MovieService movieService;

    @Autowired
    MovieDAO movieDAO;

    @Test
    void addOrUpdate_shouldNotCreateDuplicatesForSameTitleDatePress() {
        Movie m1 = new Movie();
        m1.setTitle("Duplicate Movie");
        m1.setDate("2026-01-01");
        m1.setPress("Press");
        m1.setSummary("v1");

        movieService.addOrUpdate(m1);
        assertEquals(1, movieDAO.count());

        Movie m2 = new Movie();
        m2.setTitle("Duplicate Movie");
        m2.setDate("2026-01-01");
        m2.setPress("Press");
        m2.setSummary("v2");

        movieService.addOrUpdate(m2);
        assertEquals(1, movieDAO.count());

        Movie only = movieDAO.findAll().get(0);
        assertTrue(only.getId() > 0);
        assertEquals("Duplicate Movie", only.getTitle());
        assertEquals("v2", only.getSummary());
    }
}
