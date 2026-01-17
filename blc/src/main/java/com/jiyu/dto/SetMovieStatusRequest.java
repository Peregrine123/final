package com.jiyu.dto;

import com.jiyu.pojo.MovieWatchStatus;
import lombok.Data;

@Data
public class SetMovieStatusRequest {
    private MovieWatchStatus status;
}

