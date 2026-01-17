package com.jiyu.dao;

import com.jiyu.pojo.Movie;
import com.jiyu.pojo.MovieWatchStatus;
import com.jiyu.pojo.UserMovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserMovieStatusDAO extends JpaRepository<UserMovieStatus, Long> {

    long countByUser_Id(long userId);

    long countByUser_IdAndStatus(long userId, MovieWatchStatus status);

    Optional<UserMovieStatus> findByUser_IdAndMovie_Id(long userId, int movieId);

    void deleteByUser_IdAndMovie_Id(long userId, int movieId);

    @Query("select ums.movie from UserMovieStatus ums where ums.user.id = :userId and ums.status = :status order by ums.markedAt desc")
    List<Movie> findMoviesByUserAndStatus(@Param("userId") long userId, @Param("status") MovieWatchStatus status);
}
