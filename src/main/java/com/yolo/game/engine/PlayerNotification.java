package com.yolo.game.engine;

import lombok.*;

@Data
@RequiredArgsConstructor
public class PlayerNotification {
    private final Player player;
    private final String message;
}
