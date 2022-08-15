package com.yolo.game.event.adapter;

import com.yolo.game.engine.Player;
import com.yolo.game.event.BetEvent;
import com.yolo.game.event.InvalidEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class EventFactoryImplTest {
    public static final String PAYLOAD = "{" +
            "  \"type\": \"bet\"," +
            "  \"nickname\": \"nickname-01\"," +
            "  \"number\": 5," +
            "  \"stake\": 10.5" +
            "}";
    private final EventFactoryImpl factory = new EventFactoryImpl();
    private final Player player = new Player("111", "nickname-01");
    private final Player playerNoNickname = new Player("111");

    @Test
    void shouldCreateBetEvent() {
        BetEvent betEvent = new BetEvent(player, 5, 10.5);
        assertEquals(betEvent, factory.toPlayerEvent("111", PAYLOAD));
    }

    @DisplayName("Should return InvalidEvent if type cannot be determined")
    @ParameterizedTest
    @CsvSource({
            "{\"type\":\"unsupported\"}",
            "{}",
            "}{",
    })
    void shouldCreateInvalidEvent(String payload) {
        InvalidEvent invalidEvent = new InvalidEvent(playerNoNickname, "Event type could not be determined");
        assertEquals(invalidEvent, factory.toPlayerEvent("111", payload));
    }

}