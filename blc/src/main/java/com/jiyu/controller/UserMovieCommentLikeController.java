package com.jiyu.controller;

import com.jiyu.dto.CommentLikeStateDto;
import com.jiyu.pojo.User;
import com.jiyu.service.CurrentUserService;
import com.jiyu.service.MovieCommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/movie-comment-likes")
public class UserMovieCommentLikeController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    MovieCommentLikeService movieCommentLikeService;

    @GetMapping("/{commentId}")
    public CommentLikeStateDto getState(@PathVariable long commentId) {
        User user = currentUserService.requireCurrentUser();
        return movieCommentLikeService.getState(user, commentId);
    }

    @PutMapping("/{commentId}")
    public CommentLikeStateDto like(@PathVariable long commentId) {
        User user = currentUserService.requireCurrentUser();
        return movieCommentLikeService.like(user, commentId);
    }

    @DeleteMapping("/{commentId}")
    public CommentLikeStateDto unlike(@PathVariable long commentId) {
        User user = currentUserService.requireCurrentUser();
        return movieCommentLikeService.unlike(user, commentId);
    }
}

