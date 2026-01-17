package com.jiyu.dto;

import lombok.Data;

@Data
public class UpsertMovieCommentRequest {
    private Integer rating;
    private String content;
}

