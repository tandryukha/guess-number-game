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
        config = GameConfig.builder().roundDuration(3).build();
        engine = new SimpleGameEngine(config);
    }

    @DisplayName("Should notify all online players about every round start")
    @Test
    void shouldNotifyAboutRoundStart() {
        engine.registerPlayer(player1);
        engine.subscribe(observer);
        List<PlayerNotification> startNotifications = List.of(new PlayerNotification(player1, "Round 1 started. Make your bet, you have 3 sec"));
        List<PlayerNotification> startNotifications2 = List.of(new PlayerNotification(player1, "Round 2 started. Make your bet, you have 3 sec"));

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
    //todo verify round lasts for 10 seconds - use several verifications every 10 sec verify(observer, after(10000)).onGameEvent(expectedEvent);
    //todo should register/unregister players. Once unregistered, bet should still be distributed there

}