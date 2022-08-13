package com.yolo.game.event;

public interface EventAdapter {
    PlayerEvent toPlayerEvent(String payload);
}
