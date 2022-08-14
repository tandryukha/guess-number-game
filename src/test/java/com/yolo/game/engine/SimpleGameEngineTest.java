package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import com.yolo.game.engine.random.NumberGenerator;
import com.yolo.game.event.BetEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SimpleGameEngineTest {

    public static final String ROUND_END_MESSAGE_NO_WINNERS = "Winning number in round 1 is 7. Winners:\nNo winners";
    @Mock
    private GameObserver observer;
    @Mock
    private NumberGenerator numberGenerator;
    private GameEngine engine;
    private final Player player1 = new Player("11111");
    private final Player player2 = new Player("22222");
    private final Player player3 = new Player("33333");
    private final Player player4 = new Player("44444");
    private final Player player5 = new Player("55555");
    private final BetEvent player1LosingBet = new BetEvent(player1, 1, BigDecimal.valueOf(50));
    private final BetEvent player2LosingBet = new BetEvent(player2, 2, BigDecimal.valueOf(100));

    private final BetEvent player1WinningBet = new BetEvent(player1, 7, BigDecimal.valueOf(1000));
    private final BetEvent player2WinningBet = new BetEvent(player2, 7, BigDecimal.valueOf(100));
    private final BetEvent player3LosingBet = new BetEvent(player3, 3, BigDecimal.valueOf(500));
    private final BetEvent player4LosingBet = new BetEvent(player4, 4, BigDecimal.valueOf(1000));
    private final BetEvent player5LosingBet = new BetEvent(player5, 5, BigDecimal.valueOf(50));

    private final PlayerNotification player1StartRound1Notification = new PlayerNotification(player1, "Round 1 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player1StartRound2Notification = new PlayerNotification(player1, "Round 2 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player2StartRound1Notification = new PlayerNotification(player2, "Round 1 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player3StartRound1Notification = new PlayerNotification(player3, "Round 1 started. You have 2 sec to make your bet on numbers from 1 to 10");
    private final PlayerNotification player1WinRoundNotification = new PlayerNotification(player1, "You guessed the number and won 9900.00!");
    private final PlayerNotification player2WinRound1Notification = new PlayerNotification(player2, "You guessed the number and won 990.00!");
    private final PlayerNotification player1LostRoundNotification = new PlayerNotification(player1, "You've lost in round 1");
    private final PlayerNotification player1RoundEnd1Notification = new PlayerNotification(player1, ROUND_END_MESSAGE_NO_WINNERS);
    private final PlayerNotification player2RoundEnd1Notification = new PlayerNotification(player2, ROUND_END_MESSAGE_NO_WINNERS);
    private final PlayerNotification player3RoundStats1Notification = new PlayerNotification(player3, ROUND_END_MESSAGE_NO_WINNERS);
    private final PlayerNotification player2LostRound1Notification = new PlayerNotification(player2, "You've lost in round 1");

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        GameConfig config = GameConfig.builder()
                .roundDuration(2)
                .randomNumbersCount(10)
                .winMultiplier(9.9)
                .build();
        engine = new SimpleGameEngine(config, numberGenerator);
        when(numberGenerator.generate(1, 10)).thenReturn(7);
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

    @DisplayName("At the end of the round all players should be notified about round stats when there is no winner")
    @Test
    void shouldNotifyEverybodyAboutRoundStats() throws InterruptedException {
        engine.registerPlayer(player1);
        engine.registerPlayer(player2);
        engine.registerPlayer(player3);
        final List<List<PlayerNotification>> actualNotifications = new ArrayList<>();
        CountDownLatch latch = subscribeForOneRoundEvents(actualNotifications);

        engine.start();
        engine.onEvent(player1LosingBet);
        engine.onEvent(player2LosingBet);
        latch.await();

        assertTrue(actualNotifications.contains(List.of(player1RoundEnd1Notification, player2RoundEnd1Notification, player3RoundStats1Notification)));
    }

    @DisplayName("At the end of the round losers should be notified about their loss")
    @Test
    void shouldNotifyLosersWhenLosingRound() throws InterruptedException {
        engine.registerPlayer(player1);
        engine.registerPlayer(player2);
        engine.registerPlayer(player3);
        final List<List<PlayerNotification>> actualNotifications = new ArrayList<>();
        CountDownLatch latch = subscribeForOneRoundEvents(actualNotifications);

        engine.start();
        engine.onEvent(player1LosingBet);
        engine.onEvent(player2LosingBet);
        latch.await();

        assertTrue(actualNotifications.contains(List.of(player1LostRoundNotification, player2LostRound1Notification)));
    }

    @DisplayName("At the end of the round winners should be notified about their win")
    //todo should notify winners      * * Winners are notified with the amount won and ratio to original stake
    //todo should notify all about round stats      * * All players receive a message with a list of winning players: nickname:amount
//    2) After the time expires, the server generates a random number from 1 to 10
//    3) If the player guesses the number, a message is sent to him that he won with a winnings of 9.9 times the stake
    @Test
    void shouldNotifyWinnersWithTheAmountWon() throws InterruptedException {
        engine.registerPlayer(player1);
        engine.registerPlayer(player2);
        engine.registerPlayer(player3);
        engine.registerPlayer(player4);
        engine.registerPlayer(player5);
        final List<List<PlayerNotification>> actualNotifications = new ArrayList<>();
        CountDownLatch latch = subscribeForOneRoundEvents(actualNotifications);

        engine.start();
        engine.onEvent(player1WinningBet);
        engine.onEvent(player2WinningBet);
        engine.onEvent(player3LosingBet);
        engine.onEvent(player4LosingBet);
        engine.onEvent(player5LosingBet);
        latch.await();

        assertTrue(actualNotifications.contains(List.of(player1WinRoundNotification, player2WinRound1Notification)));
    }

    //todo join most test cases into one generic and dynamic data source

    private CountDownLatch subscribeForOneRoundEvents(List<List<PlayerNotification>> actualNotifications) {
        int expectedNotificationBulksPerRound = 3;
        CountDownLatch latch = new CountDownLatch(expectedNotificationBulksPerRound);
        engine.subscribe(e -> {
            actualNotifications.add(e);
            latch.countDown();
        });
        return latch;
    }


    //todo should not accept bet with number out of range or invalid/empty bet - return error notification to the user
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