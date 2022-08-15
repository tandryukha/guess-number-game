package com.yolo.game.engine.number;

import org.apache.commons.lang3.RandomUtils;

public class RandomNumberGenerator implements NumberGenerator {
    @Override
    public int generate(int minNumber, Integer maxNumber) {
        return RandomUtils.nextInt(minNumber, maxNumber + 1);
    }
}
