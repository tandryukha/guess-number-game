package com.yolo.game.controller;

import com.yolo.game.engine.GameEngine;
import com.yolo.game.engine.GameObserver;
import com.yolo.game.engine.Player;
import com.yolo.game.engine.PlayerNotification;
import com.yolo.game.event.EventAdapter;
import com.yolo.game.event.PlayerEvent;
import jdk.internal.access.JavaIOFileDescriptorAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Slf4j
public class GameController extends TextWebSocketHandler implements GameObserver {
    private final GameEngine gameEngine;

    private final EventAdapter eventAdapter;
    private final Map<String, WebSocketSession> sessions = Collections.synchronizedMap(new HashMap<>());

    public GameController(GameEngine gameEngine, EventAdapter eventAdapter) {
        this.gameEngine = gameEngine;
        this.eventAdapter = eventAdapter;
        gameEngine.subscribe(this);
        gameEngine.start();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        gameEngine.registerPlayer(new Player(session.getId()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId(), session);
        gameEngine.removePlayer(new Player(session.getId()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        onPlayerEvent(eventAdapter.toPlayerEvent(message.getPayload()));
    }

    @Override
    public void notify(List<PlayerNotification> notifications) {
        notifications.forEach(this::notifyPlayer);
    }

    private void onPlayerEvent(PlayerEvent event) {
        Optional<PlayerNotification> notification = gameEngine.onEvent(event);
        notification.ifPresent(this::notifyPlayer);
    }

    private void notifyPlayer(PlayerNotification playerNotification) {
        WebSocketSession session = sessions.get(playerNotification.getPlayer().getId());
        try {
            session.sendMessage(new TextMessage(playerNotification.getMessage()));
        } catch (IOException e) {
            log.error("Failed to send notification: {}", playerNotification, e);
        }
    }
}
