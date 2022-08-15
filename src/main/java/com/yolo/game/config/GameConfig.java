package com.yolo.game.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Data
@Builder()
@ConfigurationProperties(prefix = "game")
@Validated
public class GameConfig {
    /**
     * Round duration in seconds
     */
    @Min(1)
    private int roundDuration;
    /**
     * Gap between rounds in seconds
     */
    @Min(0)
    private int gapBetweenRounds;
    @Min(1)
    private int minStake;
    @Min(1)
    private int maxStake;
    @Min(1)
    private int randomNumbersCount;
    private double winMultiplier;

}
