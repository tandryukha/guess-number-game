package com.yolo.game.event;

import com.yolo.game.engine.Player;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InvalidEvent implements PlayerEvent {
    private final Player player;
    private final String message;
}
