package com.yolo.game.event.adapter;

import com.yolo.game.event.PlayerEvent;

public interface EventFactory {
    PlayerEvent toPlayerEvent(String payload, String messagePayload);
}
