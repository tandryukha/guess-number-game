package com.yolo.game.engine;

import com.yolo.game.event.PlayerEvent;

import java.util.Optional;

public class SimpleGameEngine implements GameEngine {
    @Override
    public Optional<PlayerNotification> onEvent(PlayerEvent event) {
        return Optional.empty();
    }

    @Override
    public void subscribe(GameObserver observer) {

    }
}
