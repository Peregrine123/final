package com.jiyu.dao;

import com.jiyu.pojo.Movie;
import com.jiyu.pojo.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserFavoriteDAO extends JpaRepository<UserFavorite, Long> {

    long countByUser_Id(long userId);

    boolean existsByUser_IdAndMovie_Id(long userId, int movieId);

    Optional<UserFavorite> findByUser_IdAndMovie_Id(long userId, int movieId);

    void deleteByUser_IdAndMovie_Id(long userId, int movieId);

    @Query("select uf.movie from UserFavorite uf where uf.user.id = :userId order by uf.createdAt desc")
    List<Movie> findFavoriteMovies(@Param("userId") long userId);
}
