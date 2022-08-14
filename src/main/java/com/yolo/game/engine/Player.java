package com.yolo.game.engine;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Player {
    private final String id;

    private String nickname;

    public Player(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
