package com.yolo.game.event.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.game.engine.Player;
import com.yolo.game.event.BetEvent;
import com.yolo.game.event.InvalidEvent;
import com.yolo.game.event.PlayerEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class EventFactoryImpl implements EventFactory {

    public static final int INVALID_NUMBER = -1;

    @Override
    public PlayerEvent toPlayerEvent(String id, String payload) {
        Map<String, String> json = getJson(payload);
        if ("bet".equals(json.get("type"))) {
            return deserializeBetEvent(id, json);
        }
        return getInvalidEvent(id, "Event type could not be determined");
    }

    private static InvalidEvent getInvalidEvent(String id, String message) {
        return new InvalidEvent(new Player(id), message);
    }

    private static Map<String, String> getJson(String payload) {
        try {
            return new ObjectMapper().readValue(payload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return emptyMap();
        }
    }

    private PlayerEvent deserializeBetEvent(String id, Map<String, String> json) {
        String nickname = json.getOrDefault("nickname", "");
        int number = NumberUtils.toInt(json.get("number"), INVALID_NUMBER);
        double stake = NumberUtils.toDouble(json.get("stake"), INVALID_NUMBER);
        Player player = new Player(id, nickname);
        return new BetEvent(player, number, stake);
    }
}
