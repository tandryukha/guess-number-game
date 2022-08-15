package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.engine.number.NumberGenerator;
import com.yolo.game.event.BetEvent;
import com.yolo.game.event.InvalidEvent;
import com.yolo.game.event.PlayerEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Collections.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class SimpleGameEngine extends Thread implements GameEngine {
    private static final String ERROR_MESSAGE_PREFIX = "Bet not accepted. ";
    private final GameConfig config;
    private final NumberGenerator numberGenerator;
    private final List<GameObserver> observers = new ArrayList<>();
    private final Set<Player> players = synchronizedSet(new LinkedHashSet<>());
    private int round;
    private boolean active = true;
    private final Map<Integer, List<BetEvent>> roundBets = synchronizedMap(new HashMap<>());
    private final Semaphore roundMutex = new Semaphore(0);


    @Override
    public Optional<PlayerNotification> onEvent(PlayerEvent event) {
        log.info(event.toString());
        if (roundMutex.availablePermits() < 1) return getRoundNotStartedNotification(event);
        if (event instanceof BetEvent) {
            BetEvent bet = (BetEvent) event;
            Integer number = bet.getNumber();
            Optional<PlayerNotification> validationErrors = validate(bet);
            if (validationErrors.isPresent()) return validationErrors;
            roundBets.putIfAbsent(number, new ArrayList<>());
            roundBets.get(number).add(bet);
        } else if (event instanceof InvalidEvent) {
            return getInvalidEventNotification((InvalidEvent) event);
        }
        return Optional.empty();
    }

    private Optional<PlayerNotification> getInvalidEventNotification(InvalidEvent event) {
        return Optional.of(new PlayerNotification(event.getPlayer(), event.getMessage()));
    }

    private static Optional<PlayerNotification> getRoundNotStartedNotification(PlayerEvent bet) {
        return Optional.of(new PlayerNotification(bet.getPlayer(), "Bet cannot be accepted between rounds. Try again later"));
    }

    private Optional<PlayerNotification> validate(BetEvent bet) {
        String nickname = bet.getPlayer().getNickname();
        int number = bet.getNumber();
        int maxNumber = config.getMaxNumberToGenerate();
        double stake = bet.getStake();
        int minStake = config.getMinStake();
        int maxStake = config.getMaxStake();
        Optional<String> errorMessage = Optional.empty();
        if (isBlank(nickname)) {
            errorMessage = getErrorMessage("Please provide a nickname");
        } else if (number < 1 || number > maxNumber) {
            errorMessage = getErrorMessage(format("Number %s is out of range 1..%s", number, maxNumber));
        } else if (stake < minStake || stake > maxStake) {
            errorMessage = getErrorMessage(format("Stake %.2f is out of range %s..%s", stake, minStake, maxStake));
        }
        return errorMessage.map(message -> new PlayerNotification(bet.getPlayer(), message));
    }

    private static Optional<String> getErrorMessage(String message) {
        return Optional.of(ERROR_MESSAGE_PREFIX + message);
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
            TimeUnit.SECONDS.sleep(config.getGapBetweenRounds());
            log.info("{} Round {} started", getName(), round);
            roundMutex.release();
            TimeUnit.SECONDS.sleep(config.getRoundDuration());
            roundMutex.acquire();
            log.info("{} Round {} concluded", getName(), round);

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
        int winningNumber = numberGenerator.generate(1, config.getMaxNumberToGenerate());
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
        double amountWon = calculateWinning(betEvent.getStake());
        return new PlayerNotification(betEvent.getPlayer(), format("You guessed the number and won %.2f!", amountWon));
    }

    private PlayerNotification getLoserNotification(BetEvent betEvent) {
        return new PlayerNotification(betEvent.getPlayer(), format("You've lost in round %s", round));
    }

    private void startNewRound() {
        finalizeRound();//todo need to block bets once enter this method
        round++;
        List<PlayerNotification> notifications = players.stream()
                .map(this::getRoundStartNotification)
                .collect(toList());
        notifyObservers(notifications);
    }

    private PlayerNotification getRoundEndNotification(Player player, List<BetEvent> winningBets, int winningNumber) {
        String message = format("Winning number in round %s is %s. Winners:\n", round, winningNumber);
        if (winningBets.isEmpty()) {
            message += "No winners";
        } else {
            message += winningBets.stream()
                    .map(bet -> format("- %s won %.2f", bet.getPlayer().getNickname(), calculateWinning(bet.getStake())))
                    .collect(joining("\n"));
        }
        return new PlayerNotification(player, message);
    }

    private double calculateWinning(double stake) {
        return BigDecimal.valueOf(stake).multiply(BigDecimal.valueOf(config.getWinMultiplier())).doubleValue();
    }

    private PlayerNotification getRoundStartNotification(Player player) {
        String message = format("Round %s started. You have %s sec to make your bet on numbers from %s to %s",
                round, config.getRoundDuration(), 1, config.getMaxNumberToGenerate());
        return new PlayerNotification(player, message);
    }

    private void notifyObservers(List<PlayerNotification> notifications) {
        if (notifications.isEmpty()) return;
        observers.forEach(o -> o.notify(notifications));
    }
}
