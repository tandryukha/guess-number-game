package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.event.BetEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SimpleGameEngineTest {

    @Mock
    private GameObserver observer;
    private GameEngine engine;
    private final Player player1 = new Player("12345");
    private final Player player2 = new Player("22345");
    private final Player player3 = new Player("32345");
    private final BetEvent player1Bet = new BetEvent(player1, 1, BigDecimal.valueOf(50));
    private final BetEvent player2Bet = new BetEvent(player2, 2, BigDecimal.valueOf(100));
    private final PlayerNotification player1StartRound1Notification = new PlayerNotification(player1, "Round 1 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player1StartRound2Notification = new PlayerNotification(player1, "Round 2 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player2StartRound1Notification = new PlayerNotification(player2, "Round 1 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player3StartRound1Notification = new PlayerNotification(player3, "Round 1 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player1LostRoundNotification = new PlayerNotification(player1, "You've lost in round 1");
    private final PlayerNotification player1RoundEnd1Notification = new PlayerNotification(player1, "No winners in round 1");
    private final PlayerNotification player2LostRound1Notification = new PlayerNotification(player2, "You've lost in round 1");
    private final PlayerNotification player2RoundEnd1Notification = new PlayerNotification(player2, "No winners in round 1");
    private final PlayerNotification player3RoundStats1Notification = new PlayerNotification(player3, "No winners in round 1");

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        GameConfig config = GameConfig.builder()
                .roundDuration(2)
                .randomNumbers(10)
                .build();
        engine = new SimpleGameEngine(config);
    }

    @AfterEach
    void tearDown() {
        engine.terminate();
    }

    @DisplayName("Should notify all online players about every round start even if they didn't bet")
    @Test
    void shouldNotifyAboutEveryRoundStart() throws InterruptedException {
        engine.registerPlayer(player1);
        engine.subscribe(observer);

        engine.start();
        TimeUnit.SECONDS.sleep(1);

        verify(observer).notify(List.of(player1StartRound1Notification));
        verify(observer, after(3000)).notify(List.of(player1StartRound2Notification));
    }

    @DisplayName("At the end of the round should notify all players about round stats when there is no winner")
    @Test
    void shouldNotifyEverybodyAboutRoundStats() throws InterruptedException {
        engine.registerPlayer(player1);
        engine.registerPlayer(player2);
        engine.registerPlayer(player3);
        List<List<PlayerNotification>> expectedNotifications = List.of(
                List.of(player1StartRound1Notification, player2StartRound1Notification, player3StartRound1Notification),
                List.of(player1LostRoundNotification, player2LostRound1Notification),
                List.of(player1RoundEnd1Notification, player2RoundEnd1Notification, player3RoundStats1Notification)
        );
        final List<List<PlayerNotification>> actualNotifications = new ArrayList<>();
        engine.subscribe(actualNotifications::add);

        engine.start();
        engine.onEvent(player1Bet);
        engine.onEvent(player2Bet);
        TimeUnit.SECONDS.sleep(1);
        engine.terminate();
        TimeUnit.SECONDS.sleep(3);

        assertEquals(expectedNotifications, actualNotifications);
    }
        //todo should notify all about round stats      * * All players receive a message with a list of winning players: nickname:amount
    //todo should notify winners      * * Winners are notified with the amount won and ratio to original stake


    //todo should not accept bet with number out of range or invalid/empty bet - re
    // turn out of range notification
    //todo should not accept bets between rounds     //todo don't accept players when round not started yet - send message back to them * If there is no active round, user is notified about refused bet


    /*
    **Game process:**

1) The server starts a round of the game and gives 10 seconds to place a bet for the players on numbers from 1 to 10 with the **amount of the bet**

2) After the time expires, the server generates a random number from 1 to 10

3) If the player guesses the number, a message is sent to him that he won with a winnings of 9.9 times the stake

4) If the player loses receives a message about the loss

5) All players receive a message with a list of winning players in which there is a nickname and the amount of winnings
     */
}