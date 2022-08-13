package com.yolo.game.event;

import com.yolo.game.engine.Player;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BetEvent implements PlayerEvent {
    private Player player;
    private BigDecimal stake;
}
