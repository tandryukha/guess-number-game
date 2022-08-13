package com.yolo.game.controller;

import com.yolo.game.engine.GameEngine;
import com.yolo.game.engine.GameObserver;
import com.yolo.game.engine.PlayerNotification;
import com.yolo.game.event.EventAdapter;
import com.yolo.game.event.GameEvent;
import com.yolo.game.event.PlayerEvent;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is actually adapter between socket and game
 */
public class GameController extends TextWebSocketHandler implements GameObserver {
    private final List<WebSocketSession> onlineUsers = new CopyOnWriteArrayList<>();
    private final GameEngine gameEngine;

    public GameController(GameEngine gameEngine, EventAdapter eventAdapter) {
        this.gameEngine = gameEngine;
        this.eventAdapter = eventAdapter;
        gameEngine.subscribe(this);
    }

    private final EventAdapter eventAdapter;

    /**
     * Players can connect at any time then want
     * Once they connect they're notified when round starts/ends and results even if he didn't bet
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        onlineUsers.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        onlineUsers.remove(session);//todo may not be optimal to scan the whole list
    }

    /**
     * Players can bet at any time they want
     * Game accepts bet only if there is an active round
     * If there is no active round, user is notified about refused bet
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        onPlayerEvent(eventAdapter.toPlayerEvent(message.getPayload()));
        //todo don't accept players when round not started yet - send message back to them
        //game rond starts automatically when 2+ players connect?
    }

    @Override
    public void onGameEvent(GameEvent event) {
        List<PlayerNotification> notifications = event.getNotifications();
        notifications.forEach(this::notifyPlayer);
    }

    private void onPlayerEvent(PlayerEvent event) {
        Optional<PlayerNotification> notification = gameEngine.onEvent(event);
        notification.ifPresent(this::notifyPlayer);
    }

    /**
     * Then sharing results with players:
     * * Winners are notified with the amount won and ratio to original stake
     * * Losers are notified about the loss
     * * All players receive a message with a list of winning players: nickname:amount
     * Send notification to a user by session id
     */
    private void notifyPlayer(PlayerNotification playerNotification) {
        //todo
    }
}
