package com.jiyu.dao;

import com.jiyu.pojo.MovieCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieCommentLikeDAO extends JpaRepository<MovieCommentLike, Long> {

    long countByComment_Id(long commentId);

    boolean existsByComment_IdAndUser_Id(long commentId, long userId);

    void deleteByComment_IdAndUser_Id(long commentId, long userId);

    @Query("select mcl.comment.id, count(mcl) from MovieCommentLike mcl where mcl.comment.id in :commentIds group by mcl.comment.id")
    List<Object[]> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);

    @Query("select mcl.comment.id from MovieCommentLike mcl where mcl.user.id = :userId and mcl.comment.id in :commentIds")
    List<Long> findLikedCommentIds(@Param("userId") long userId, @Param("commentIds") List<Long> commentIds);
}

