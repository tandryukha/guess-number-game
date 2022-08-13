package com.yolo.game.engine;

import lombok.Data;

@Data
public class PlayerNotification {
    private Player player;
    private String message;
}
