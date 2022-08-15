package com.yolo.game.engine.number;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomNumberGeneratorTest {
    private final RandomNumberGenerator generator = new RandomNumberGenerator();

    @Test
    void shouldGenerateOneGivenNoRange() {
        assertEquals(1, generator.generate(1, 1));
    }

    @Test
    void shouldGenerateRandomWithinRange() {
        Integer generated = generator.generate(1, 2);
        assertTrue(generated.equals(1) || generated.equals(2));
    }
}