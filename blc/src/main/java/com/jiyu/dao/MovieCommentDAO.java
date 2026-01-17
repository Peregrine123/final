package com.jiyu.dao;

import com.jiyu.pojo.MovieComment;
import com.jiyu.pojo.MovieCommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieCommentDAO extends JpaRepository<MovieComment, Long> {

    Optional<MovieComment> findByMovie_IdAndUser_Id(int movieId, long userId);

    Optional<MovieComment> findFirstByUser_IdOrderByUpdatedAtDesc(long userId);

    Page<MovieComment> findByMovie_Id(int movieId, Pageable pageable);

    Page<MovieComment> findByMovie_IdAndStatus(int movieId, MovieCommentStatus status, Pageable pageable);

    @Query("select avg(mc.rating) from MovieComment mc where mc.movie.id = :movieId and mc.rating is not null")
    Double avgApprovedRating(@Param("movieId") int movieId);

    @Query("select count(mc) from MovieComment mc where mc.movie.id = :movieId and mc.rating is not null")
    long countApprovedRatings(@Param("movieId") int movieId);
}
