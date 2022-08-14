package com.yolo.game.engine;

import com.yolo.game.config.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.verify;

class SimpleGameEngineTest {

    @Mock
    private GameObserver observer;
    private GameConfig config;
    private GameEngine engine;
    private final Player player1 = new Player("12345");

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        config = GameConfig.builder().roundDuration(2).build();
        engine = new SimpleGameEngine(config);
    }

    @DisplayName("Should notify all online players about every round start")
    @Test
    void shouldNotifyAboutRoundStart() {
        engine.registerPlayer(player1);
        engine.subscribe(observer);
        List<PlayerNotification> startNotifications = List.of(new PlayerNotification(player1, "Round 1 started. Make your bet, you have 2 sec"));
        List<PlayerNotification> startNotifications2 = List.of(new PlayerNotification(player1, "Round 2 started. Make your bet, you have 2 sec"));

        engine.start();

        verify(observer).notify(startNotifications);
        verify(observer, after(3000)).notify(startNotifications2);
    }

    //todo should notify all online players about round end * Once they connect they're notified when round starts/ends and results even if he didn't bet
    //todo should accept bets during round * Game accepts bet only if there is an active round
    //todo should not accept bets between rounds     //todo don't accept players when round not started yet - send message back to them * If there is no active round, user is notified about refused bet
    //todo should notify losers      * * Losers are notified about the loss
    //todo should notify winners      * * Winners are notified with the amount won and ratio to original stake
    //todo should notify all about round stats      * * All players receive a message with a list of winning players: nickname:amount
    //todo should register/unregister players. Once unregistered, bet should still be distributed there

    /*
    **Game process:**

1) The server starts a round of the game and gives 10 seconds to place a bet for the players on numbers from 1 to 10 with the **amount of the bet**

2) After the time expires, the server generates a random number from 1 to 10

3) If the player guesses the number, a message is sent to him that he won with a winnings of 9.9 times the stake

4) If the player loses receives a message about the loss

5) All players receive a message with a list of winning players in which there is a nickname and the amount of winnings
     */
}