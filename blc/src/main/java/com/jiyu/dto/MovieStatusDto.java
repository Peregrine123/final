package com.jiyu.dto;

import com.jiyu.pojo.MovieWatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieStatusDto {
    private MovieWatchStatus status;
    private Instant markedAt;
}

