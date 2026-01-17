package com.jiyu.controller;

import com.jiyu.dto.MovieStatusDto;
import com.jiyu.dto.SetMovieStatusRequest;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.MovieWatchStatus;
import com.jiyu.pojo.User;
import com.jiyu.service.CurrentUserService;
import com.jiyu.service.UserMovieStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/movie-status")
public class UserMovieStatusController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    UserMovieStatusService userMovieStatusService;

    @GetMapping("/{movieId}")
    public MovieStatusDto getMovieStatus(@PathVariable int movieId) {
        User user = currentUserService.requireCurrentUser();
        return userMovieStatusService.getStatus(user, movieId);
    }

    @PutMapping("/{movieId}")
    public MovieStatusDto setMovieStatus(@PathVariable int movieId, @RequestBody SetMovieStatusRequest request) {
        User user = currentUserService.requireCurrentUser();
        return userMovieStatusService.setStatus(user, movieId, request == null ? null : request.getStatus());
    }

    @GetMapping
    public List<Movie> listMoviesByStatus(@RequestParam("status") MovieWatchStatus status) {
        User user = currentUserService.requireCurrentUser();
        return userMovieStatusService.listByStatus(user, status);
    }
}
