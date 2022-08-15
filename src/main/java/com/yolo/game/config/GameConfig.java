package com.yolo.game.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "game")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    /**
     * Max number to generate
     */
    @Min(1)
    private int maxNumberToGenerate;
    /**
     * Coefficient to determine amount of win relative to stake
     */
    @Min(0)
    private double winMultiplier;

}
