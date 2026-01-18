package com.jiyu.dao;

import com.jiyu.pojo.Category;
import com.jiyu.pojo.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieDAO extends JpaRepository<Movie,Integer> {
    List<Movie> findAllByCategory(Category category);
    List<Movie> findAllByTitleLikeOrCastLike(String keyword1, String keyword2);

    Optional<Movie> findFirstByTitle(String title);
    Optional<Movie> findFirstByTitleAndDate(String title, String date);
    Optional<Movie> findFirstByTitleAndDateAndPress(String title, String date, String press);
}
