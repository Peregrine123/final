package com.jiyu.dto;

import com.jiyu.pojo.MovieCommentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCommentDto {
    private long id;
    private int movieId;
    private long userId;
    private String username;
    private Integer rating;
    private String content;
    private MovieCommentStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    // likes
    private long likeCount;
    private boolean likedByMe;
}
