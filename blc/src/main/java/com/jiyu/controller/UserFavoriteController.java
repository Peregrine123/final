package com.jiyu.controller;

import com.jiyu.dto.FavoriteStateDto;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.User;
import com.jiyu.service.CurrentUserService;
import com.jiyu.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/favorites")
public class UserFavoriteController {

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    UserFavoriteService userFavoriteService;

    @GetMapping
    public List<Movie> listMyFavorites() {
        User user = currentUserService.requireCurrentUser();
        return userFavoriteService.listFavorites(user);
    }

    @GetMapping("/{movieId}")
    public FavoriteStateDto getFavoriteState(@PathVariable int movieId) {
        User user = currentUserService.requireCurrentUser();
        return userFavoriteService.getFavoriteState(user, movieId);
    }

    @PutMapping("/{movieId}")
    public FavoriteStateDto addFavorite(@PathVariable int movieId) {
        User user = currentUserService.requireCurrentUser();
        return userFavoriteService.addFavorite(user, movieId);
    }

    @DeleteMapping("/{movieId}")
    public FavoriteStateDto removeFavorite(@PathVariable int movieId) {
        User user = currentUserService.requireCurrentUser();
        return userFavoriteService.removeFavorite(user, movieId);
    }
}

