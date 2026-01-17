package com.jiyu.service;

import com.jiyu.dao.MovieDAO;
import com.jiyu.dao.UserMovieStatusDAO;
import com.jiyu.dto.MovieStatusDto;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.MovieWatchStatus;
import com.jiyu.pojo.User;
import com.jiyu.pojo.UserMovieStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserMovieStatusService {

    @Autowired
    UserMovieStatusDAO userMovieStatusDAO;

    @Autowired
    MovieDAO movieDAO;

    public MovieStatusDto getStatus(User user, int movieId) {
        Optional<UserMovieStatus> existing = userMovieStatusDAO.findByUser_IdAndMovie_Id(user.getId(), movieId);
        return existing
                .map(e -> new MovieStatusDto(e.getStatus(), e.getMarkedAt()))
                .orElseGet(() -> new MovieStatusDto(null, null));
    }

    public List<Movie> listByStatus(User user, MovieWatchStatus status) {
        return userMovieStatusDAO.findMoviesByUserAndStatus(user.getId(), status);
    }

    @Transactional
    public MovieStatusDto setStatus(User user, int movieId, MovieWatchStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status 不能为空");
        }

        Movie movie = movieDAO.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "电影不存在"));

        Optional<UserMovieStatus> existing = userMovieStatusDAO.findByUser_IdAndMovie_Id(user.getId(), movieId);
        if (existing.isPresent()) {
            UserMovieStatus e = existing.get();
            if (e.getStatus() == status) {
                // Idempotent: keep original markedAt.
                return new MovieStatusDto(e.getStatus(), e.getMarkedAt());
            }
            e.setStatus(status);
            e.setMarkedAt(Instant.now());
            userMovieStatusDAO.save(e);
            return new MovieStatusDto(e.getStatus(), e.getMarkedAt());
        }

        UserMovieStatus created = new UserMovieStatus();
        created.setUser(user);
        created.setMovie(movie);
        created.setStatus(status);
        created.setMarkedAt(Instant.now());
        userMovieStatusDAO.save(created);
        return new MovieStatusDto(created.getStatus(), created.getMarkedAt());
    }

    @Transactional
    public MovieStatusDto markWatched(User user, int movieId) {
        return setStatus(user, movieId, MovieWatchStatus.WATCHED);
    }
}

