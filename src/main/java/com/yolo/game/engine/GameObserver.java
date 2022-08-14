package com.yolo.game.engine;

import java.util.List;

public interface GameObserver {
    void notify(List<PlayerNotification> notifications);
}
