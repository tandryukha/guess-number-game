package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.event.PlayerEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class SimpleGameEngine extends Thread implements GameEngine {
    private final GameConfig config;
    private final List<GameObserver> observers = new ArrayList<>();
    private final Set<Player> players = new HashSet<>();
    private int round;
    private boolean active = true;

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

    @SneakyThrows
    @Override
    public void run() {
        log.info("Starting thread {} round={}", getName(), round);
        while (active) {
            finalizeRound();
            startNewRound();
            TimeUnit.SECONDS.sleep(config.getRoundDuration());
        }
    }

    @Override
    public void terminate() {
        active = false;
    }

    private void finalizeRound() {
        if (round < 1) return;
    }

    private void startNewRound() {
        log.info("Thread {} finished round={}", getName(), round);
        round++;
        List<PlayerNotification> notifications = players.stream()
                .map(this::getRoundStartNotification)
                .collect(toList());
        notifyObservers(notifications);
    }

    private PlayerNotification getRoundStartNotification(Player player) {
        String message = format("Round %s started. You have %s sec to make your bet on numbers from %s to %s",
                round,
                config.getRoundDuration(),
                1, config.getRandomNumbers()
        );
        return new PlayerNotification(player, message);
    }

    private void notifyObservers(List<PlayerNotification> notifications) {
        observers.forEach(o -> o.notify(notifications));
    }
}
