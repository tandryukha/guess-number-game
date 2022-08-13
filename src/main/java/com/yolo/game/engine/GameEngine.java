package com.yolo.game.engine;

import com.yolo.game.event.PlayerEvent;

import java.util.Optional;

public interface GameEngine {
    Optional<PlayerNotification> onEvent(PlayerEvent event);

    void subscribe(GameObserver observer);
}
