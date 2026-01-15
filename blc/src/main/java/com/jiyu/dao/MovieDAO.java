package com.jiyu.dao;

import com.jiyu.pojo.Category;
import com.jiyu.pojo.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieDAO extends JpaRepository<Movie,Integer> {
    List<Movie> findAllByCategory(Category category);
    List<Movie> findAllByTitleLikeOrCastLike(String keyword1, String keyword2);
}
