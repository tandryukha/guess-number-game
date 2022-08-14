package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.event.BetEvent;
import com.yolo.game.event.PlayerEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class SimpleGameEngine extends Thread implements GameEngine {
    private final GameConfig config;
    private final List<GameObserver> observers = new ArrayList<>();
    private final Set<Player> players = new LinkedHashSet<>();
    private int round;
    private boolean active = true;
    private final Map<Integer, List<BetEvent>> roundBets = new HashMap<>();

    @Override
    public Optional<PlayerNotification> onEvent(PlayerEvent event) {
        if (event instanceof BetEvent) {
            BetEvent bet = (BetEvent) event;
            Integer number = bet.getNumber();
            Integer maxNumber = config.getRandomNumbers();
            if (number < 1 || number > maxNumber) {
                return Optional.of(getOutOfRangeNotification(bet, number, maxNumber));
            }
            roundBets.putIfAbsent(number, new ArrayList<>());
            roundBets.get(number).add(bet);
        }
        return Optional.empty();
    }

    private static PlayerNotification getOutOfRangeNotification(BetEvent bet, Integer number, Integer maxNumber) {
        return new PlayerNotification(bet.getPlayer(), format("Number %s is out of range 1..%s", number, maxNumber));
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
        log.info("Starting engine thread {}", getName());
        while (active) {
            startNewRound();
            TimeUnit.SECONDS.sleep(config.getRoundDuration());
        }
        finalizeRound();
        log.info("Exiting engine thread {}", getName());
    }

    @Override
    public void terminate() {
        active = false;
    }

    private void finalizeRound() {
        if (round < 1 || roundBets.isEmpty()) return;
        Integer winningNumber = -1;
        List<BetEvent> winners = Optional.ofNullable(roundBets.remove(winningNumber)).orElse(emptyList());
        List<BetEvent> losers = roundBets.values().stream().flatMap(Collection::stream).collect(toList());

        List<PlayerNotification> winnerNotifications = winners.stream().map(this::toWinnerNotification).collect(toList());
        notifyObservers(winnerNotifications);

        List<PlayerNotification> loserNotifications = losers.stream().map(this::getLoserNotification).collect(toList());
        notifyObservers(loserNotifications);

        List<PlayerNotification> endRoundNotifications = players.stream()
                .map(player -> getRoundEndNotification(player, winners, winningNumber))
                .collect(toList());
        notifyObservers(endRoundNotifications);
        roundBets.clear();
    }

    private PlayerNotification toWinnerNotification(BetEvent betEvent) {
        throw new UnsupportedOperationException("not implemented yet");//todo
    }

    private PlayerNotification getLoserNotification(BetEvent betEvent) {
        return new PlayerNotification(betEvent.getPlayer(), format("You've lost in round %s", round));
    }

    private void startNewRound() {
        finalizeRound();
        round++;
        List<PlayerNotification> notifications = players.stream()
                .map(this::getRoundStartNotification)
                .collect(toList());
        notifyObservers(notifications);
    }

    private PlayerNotification getRoundEndNotification(Player player, List<BetEvent> winners, Integer winningNumber) {
        if (winners.isEmpty()) return new PlayerNotification(player, format("No winners in round %s", round));
        throw new UnsupportedOperationException("not implemented yet");//todo
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
        if (notifications.isEmpty()) return;
        observers.forEach(o -> o.notify(notifications));
    }
}
