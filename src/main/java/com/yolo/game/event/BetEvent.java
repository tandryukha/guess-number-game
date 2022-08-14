package com.yolo.game.event;

import com.yolo.game.engine.Player;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class BetEvent implements PlayerEvent {
    private final Player player;
    private final Integer number ;
    private final BigDecimal stake;
}
