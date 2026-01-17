package com.jiyu.controller;

import com.jiyu.dto.MovieCommentDto;
import com.jiyu.dto.UpsertMovieCommentRequest;
import com.jiyu.pojo.User;
import com.jiyu.service.CurrentUserService;
import com.jiyu.service.MovieCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/movie-comments")
public class MyMovieCommentController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    MovieCommentService movieCommentService;

    @GetMapping("/{movieId}")
    public MovieCommentDto getMyComment(@PathVariable int movieId) {
        User user = currentUserService.requireCurrentUser();
        return movieCommentService.getMyComment(user, movieId);
    }

    @PutMapping("/{movieId}")
    public MovieCommentDto upsertMyComment(@PathVariable int movieId, @RequestBody(required = false) UpsertMovieCommentRequest request) {
        User user = currentUserService.requireCurrentUser();
        Integer rating = request == null ? null : request.getRating();
        String content = request == null ? null : request.getContent();
        return movieCommentService.upsertMyComment(user, movieId, rating, content);
    }
}

