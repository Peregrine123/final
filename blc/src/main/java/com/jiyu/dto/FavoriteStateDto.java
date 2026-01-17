package com.jiyu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStateDto {
    private boolean favorited;
    private Instant createdAt;
}

