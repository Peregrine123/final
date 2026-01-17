package com.jiyu.service;

import com.jiyu.dao.MovieCommentDAO;
import com.jiyu.dao.MovieCommentLikeDAO;
import com.jiyu.dto.CommentLikeStateDto;
import com.jiyu.pojo.MovieComment;
import com.jiyu.pojo.MovieCommentLike;
import com.jiyu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovieCommentLikeService {

    @Autowired
    MovieCommentLikeDAO movieCommentLikeDAO;

    @Autowired
    MovieCommentDAO movieCommentDAO;

    private MovieComment requireComment(long commentId) {
        return movieCommentDAO.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "短评不存在"));
    }

    private CommentLikeStateDto state(User user, long commentId) {
        long count = movieCommentLikeDAO.countByComment_Id(commentId);
        boolean liked = user != null && movieCommentLikeDAO.existsByComment_IdAndUser_Id(commentId, user.getId());
        return new CommentLikeStateDto(liked, count);
    }

    @Transactional(readOnly = true)
    public CommentLikeStateDto getState(User user, long commentId) {
        requireComment(commentId);
        return state(user, commentId);
    }

    @Transactional
    public CommentLikeStateDto like(User user, long commentId) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        MovieComment comment = requireComment(commentId);

        if (movieCommentLikeDAO.existsByComment_IdAndUser_Id(commentId, user.getId())) {
            return state(user, commentId);
        }

        MovieCommentLike like = new MovieCommentLike();
        like.setUser(user);
        like.setComment(comment);
        try {
            movieCommentLikeDAO.save(like);
        } catch (DataIntegrityViolationException ignore) {
            // Race: unique constraint hit. Treat as success.
        }
        return state(user, commentId);
    }

    @Transactional
    public CommentLikeStateDto unlike(User user, long commentId) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        requireComment(commentId);

        // Idempotent: deleting a missing row should not error.
        movieCommentLikeDAO.deleteByComment_IdAndUser_Id(commentId, user.getId());
        return state(user, commentId);
    }
}

