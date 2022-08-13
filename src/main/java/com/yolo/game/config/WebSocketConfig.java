package com.yolo.game.config;

import com.yolo.game.controller.GameController;
import com.yolo.game.engine.GameEngine;
import com.yolo.game.event.EventAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameEngine gameEngine;
    private final EventAdapter eventAdapter;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameController(gameEngine, eventAdapter), "/game/guessNumber");
    }
}
