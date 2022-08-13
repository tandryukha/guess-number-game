package com.yolo.game.engine;

import com.yolo.game.event.GameEvent;

public interface GameObserver {
    void onGameEvent(GameEvent event);
}
