package com.jiyu.service;

import com.jiyu.dao.MovieDAO;
import com.jiyu.dao.UserFavoriteDAO;
import com.jiyu.dto.FavoriteStateDto;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.User;
import com.jiyu.pojo.UserFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserFavoriteService {

    @Autowired
    UserFavoriteDAO userFavoriteDAO;

    @Autowired
    MovieDAO movieDAO;

    public List<Movie> listFavorites(User user) {
        return userFavoriteDAO.findFavoriteMovies(user.getId());
    }

    public FavoriteStateDto getFavoriteState(User user, int movieId) {
        Optional<UserFavorite> existing = userFavoriteDAO.findByUser_IdAndMovie_Id(user.getId(), movieId);
        return existing
                .map(uf -> new FavoriteStateDto(true, uf.getCreatedAt()))
                .orElseGet(() -> new FavoriteStateDto(false, null));
    }

    @Transactional
    public FavoriteStateDto addFavorite(User user, int movieId) {
        Movie movie = movieDAO.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "电影不存在"));

        Optional<UserFavorite> existing = userFavoriteDAO.findByUser_IdAndMovie_Id(user.getId(), movieId);
        if (existing.isPresent()) {
            // Idempotent: keep original createdAt.
            return new FavoriteStateDto(true, existing.get().getCreatedAt());
        }

        UserFavorite uf = new UserFavorite();
        uf.setUser(user);
        uf.setMovie(movie);
        try {
            userFavoriteDAO.save(uf);
        } catch (DataIntegrityViolationException ignore) {
            // Rare race: unique constraint hit. Treat as success.
        }
        return getFavoriteState(user, movieId);
    }

    @Transactional
    public FavoriteStateDto removeFavorite(User user, int movieId) {
        // Idempotent: delete missing row should not error.
        userFavoriteDAO.deleteByUser_IdAndMovie_Id(user.getId(), movieId);
        return new FavoriteStateDto(false, null);
    }
}

