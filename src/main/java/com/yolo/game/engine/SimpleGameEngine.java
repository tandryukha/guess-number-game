package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.event.PlayerEvent;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class SimpleGameEngine implements GameEngine {
    private final GameConfig config;

    @Override
    public Optional<PlayerNotification> onEvent(PlayerEvent event) {
        return Optional.empty();
    }

    @Override
    public void subscribe(GameObserver observer) {

    }

    @Override
    public void start() {

    }

    @Override
    public void registerPlayer(Player player) {

    }

    @Override
    public void removePlayer(Player player) {

    }
}
