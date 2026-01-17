package com.jiyu.service;

import com.jiyu.dao.MovieDAO;
import com.jiyu.dao.UserFavoriteDAO;
import com.jiyu.dao.UserMovieStatusDAO;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.MovieWatchStatus;
import com.jiyu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Demo helper: seed a brand-new user's favorites / wish / watched lists
 * so the UI has content during presentations.
 */
@Service
public class DemoUserActionsSeeder {

    @Value("${blc.demo.seed-user-actions:false}")
    private boolean enabled;

    @Value("${blc.demo.seed-user-actions.favorites:6}")
    private int favoritesCount;

    @Value("${blc.demo.seed-user-actions.wish:4}")
    private int wishCount;

    @Value("${blc.demo.seed-user-actions.watched:4}")
    private int watchedCount;

    @Autowired
    UserFavoriteDAO userFavoriteDAO;

    @Autowired
    UserMovieStatusDAO userMovieStatusDAO;

    @Autowired
    MovieDAO movieDAO;

    @Autowired
    UserFavoriteService userFavoriteService;

    @Autowired
    UserMovieStatusService userMovieStatusService;

    @Transactional
    public void seedIfEmpty(User user) {
        if (!enabled || user == null) {
            return;
        }

        long userId = user.getId();
        int favTarget = Math.max(0, favoritesCount);
        int wishTarget = Math.max(0, wishCount);
        int watchedTarget = Math.max(0, watchedCount);

        int favSeeded = (int) userFavoriteDAO.countByUser_Id(userId);
        int wishSeeded = (int) userMovieStatusDAO.countByUser_IdAndStatus(userId, MovieWatchStatus.WISH);
        int watchedSeeded = (int) userMovieStatusDAO.countByUser_IdAndStatus(userId, MovieWatchStatus.WATCHED);

        // Nothing to do.
        if (favSeeded >= favTarget && wishSeeded >= wishTarget && watchedSeeded >= watchedTarget) {
            return;
        }

        List<Movie> movies = movieDAO.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (movies.isEmpty()) {
            return;
        }

        // Keep seeded movies disjoint across the three tabs for clearer demos.
        Set<Integer> used = new HashSet<>();
        if (favSeeded > 0) {
            for (Movie m : userFavoriteDAO.findFavoriteMovies(userId)) {
                if (m != null) {
                    used.add(m.getId());
                }
            }
        }
        if (wishSeeded > 0) {
            for (Movie m : userMovieStatusDAO.findMoviesByUserAndStatus(userId, MovieWatchStatus.WISH)) {
                if (m != null) {
                    used.add(m.getId());
                }
            }
        }
        if (watchedSeeded > 0) {
            for (Movie m : userMovieStatusDAO.findMoviesByUserAndStatus(userId, MovieWatchStatus.WATCHED)) {
                if (m != null) {
                    used.add(m.getId());
                }
            }
        }

        for (Movie movie : movies) {
            if (movie == null) {
                continue;
            }
            int movieId = movie.getId();
            if (used.contains(movieId)) {
                continue;
            }

            if (favSeeded < favTarget) {
                userFavoriteService.addFavorite(user, movieId);
                favSeeded++;
                used.add(movieId);
                continue;
            }
            if (wishSeeded < wishTarget) {
                userMovieStatusService.setStatus(user, movieId, MovieWatchStatus.WISH);
                wishSeeded++;
                used.add(movieId);
                continue;
            }
            if (watchedSeeded < watchedTarget) {
                userMovieStatusService.setStatus(user, movieId, MovieWatchStatus.WATCHED);
                watchedSeeded++;
                used.add(movieId);
                continue;
            }

            // All reached targets.
            break;
        }
    }
}
