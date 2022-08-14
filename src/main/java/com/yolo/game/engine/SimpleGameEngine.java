package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.event.PlayerEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class SimpleGameEngine extends Thread implements GameEngine {
    private final GameConfig config;
    private final List<GameObserver> observers = new ArrayList<>();
    private final Set<Player> players = new HashSet<>();
    private int round;

    @Override
    public Optional<PlayerNotification> onEvent(PlayerEvent event) {
        return Optional.empty();
    }

    @Override
    public void subscribe(GameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void registerPlayer(Player player) {
        players.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            finalizeRound();
            startNewRound();
            Thread.sleep(config.getRoundDuration() * 1000);
        }
    }

    private void finalizeRound() {
        if (round < 1) return;
    }

    private void startNewRound() {
        round++;
        List<PlayerNotification> notifications = players.stream()
                .map(this::getRoundStartNotification)
                .collect(toList());
        notifyObservers(notifications);
    }

    private PlayerNotification getRoundStartNotification(Player player) {
        String message = format("Round %s started. Make your bet, you have %s sec", round, config.getRoundDuration());
        return new PlayerNotification(player, message);
    }

    private void notifyObservers(List<PlayerNotification> notifications) {
        observers.forEach(o -> o.notify(notifications));
    }
}
