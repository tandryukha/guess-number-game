package com.yolo.game;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.yolo.game.config.GameConfig;
import com.yolo.game.engine.GameEngine;
import com.yolo.game.engine.number.NumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class GameControllerTest {
    @MockBean
    NumberGenerator numberGenerator;
    @Autowired
    GameConfig config;
    @Autowired
    GameEngine engine;
    private static final String LOSING_BET = "{" +
            "  \"type\": \"bet\"," +
            "  \"nickname\": \"nickname-01\"," +
            "  \"number\": 5," +
            "  \"stake\": 10.5" +
            "}";

    public static final String WINNING_BET = "{" +
            "  \"type\": \"bet\"," +
            "  \"nickname\": \"nickname-01\"," +
            "  \"number\": 7," +
            "  \"stake\": 10.5" +
            "}";


    @BeforeEach
    void setUp() {
        when(numberGenerator.generate(1, 10)).thenReturn(7);
    }

    @LocalServerPort
    int port;

    @Test
    void socketShouldOpen() throws IOException, WebSocketException {
        WebSocket socket = openSocket();
        assertTrue(socket.isOpen());
    }

    private WebSocket openSocket() throws WebSocketException, IOException {
        return new WebSocketFactory()
                .createSocket(String.format("ws://localhost:%s/game/guessNumber", port))
                .connect();
    }

    @DisplayName("Single player should win in case he guesses the number")
    @Test
    void playerShouldWin() throws Exception {
        WebSocket socket = openSocket();
        List<String> notifications = addPlayer(socket);
        List<String> expectedNotifications = List.of(
                "You guessed the number and won 103.95!",
                "Winning number in round 1 is 7. Winners:\n" +
                        "- nickname-01 won 103.95",
                "Round 2 started. You have 3 sec to make your bet on numbers from 1 to 10"
        );

        TimeUnit.SECONDS.sleep(config.getGapBetweenRounds());
        socket.sendText(WINNING_BET);
        TimeUnit.SECONDS.sleep(config.getRoundDuration());

        assertEquals(expectedNotifications, notifications);
    }

    @DisplayName("Single player should lose in case he doesn't guess the number")
    @Test
    void playerShouldLose() throws Exception {
        WebSocket socket = openSocket();
        List<String> notifications = addPlayer(socket);
        List<String> expectedNotifications = List.of(
                "You've lost in round 1",
                "Winning number in round 1 is 7. Winners:\nNo winners",
                "Round 2 started. You have 3 sec to make your bet on numbers from 1 to 10"
        );

        TimeUnit.SECONDS.sleep(config.getGapBetweenRounds());
        socket.sendText(LOSING_BET);
        TimeUnit.SECONDS.sleep(config.getRoundDuration());

        assertEquals(expectedNotifications, notifications);
    }

    private static List<String> addPlayer(WebSocket socket) {
        List<String> notifications = new ArrayList<>();
        socket.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String text) {
                notifications.add(text);
            }
        });
        return notifications;
    }
}
