package com.jiyu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCommentPageDto {
    private List<MovieCommentDto> items;
    private long total;
    private int page;
    private int size;
}

