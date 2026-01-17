package com.jiyu.controller;

import com.jiyu.dto.MovieCommentDto;
import com.jiyu.dto.SetCommentStatusRequest;
import com.jiyu.pojo.MovieCommentStatus;
import com.jiyu.pojo.User;
import com.jiyu.service.CurrentUserService;
import com.jiyu.service.MovieCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/movie-comments")
public class AdminMovieCommentController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    MovieCommentService movieCommentService;

    @PutMapping("/{commentId}/status")
    public MovieCommentDto setStatus(@PathVariable long commentId, @RequestBody(required = false) SetCommentStatusRequest request) {
        User current = currentUserService.requireCurrentUser();
        movieCommentService.requireAdmin(current);
        MovieCommentStatus status = request == null ? null : request.getStatus();
        return movieCommentService.setCommentStatus(commentId, status);
    }
}

