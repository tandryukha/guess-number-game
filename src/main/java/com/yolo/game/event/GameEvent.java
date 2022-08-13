package com.yolo.game.event;

import com.yolo.game.engine.PlayerNotification;
import lombok.Data;

import java.util.List;

@Data
public class GameEvent {
    private List<PlayerNotification> notifications;
}
