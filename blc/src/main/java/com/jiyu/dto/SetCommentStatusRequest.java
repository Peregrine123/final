package com.jiyu.dto;

import com.jiyu.pojo.MovieCommentStatus;
import lombok.Data;

@Data
public class SetCommentStatusRequest {
    private MovieCommentStatus status;
}

