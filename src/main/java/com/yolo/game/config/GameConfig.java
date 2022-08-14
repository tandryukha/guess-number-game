package com.yolo.game.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameConfig {
    /**
     * Round duration in seconds
     */
    private int roundDuration;
}
