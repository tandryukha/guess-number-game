package com.yolo.game.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ConfigurationProperties(prefix = "game")
@Validated
public class GameConfig {
    /**
     * Round duration in seconds
     */
    @NotNull
    @Min(1)
    private Integer roundDuration;
}
