package com.jiyu.controller;

import com.jiyu.dto.MovieCommentPageDto;
import com.jiyu.dto.RatingSummaryDto;
import com.jiyu.pojo.User;
import com.jiyu.service.CurrentUserService;
import com.jiyu.service.MovieCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/{movieId}")
public class MovieCommentPublicController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    MovieCommentService movieCommentService;

    @GetMapping("/comments")
    public MovieCommentPageDto listApprovedComments(
            @PathVariable int movieId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        User user = currentUserService.requireCurrentUser();
        return movieCommentService.listApprovedComments(user, movieId, page, size);
    }

    @GetMapping("/rating-summary")
    public RatingSummaryDto getRatingSummary(@PathVariable int movieId) {
        return movieCommentService.getRatingSummary(movieId);
    }
}
