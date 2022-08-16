package com.yolo.game.config;

import com.yolo.game.controller.GameController;
import com.yolo.game.engine.GameEngine;
import com.yolo.game.engine.SimpleGameEngine;
import com.yolo.game.engine.number.NumberGenerator;
import com.yolo.game.engine.number.RandomNumberGenerator;
import com.yolo.game.event.adapter.EventFactory;
import com.yolo.game.event.adapter.EventFactoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.PostConstruct;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ContextConfig {

    @Bean
    public GameController gameController(GameEngine gameEngine, EventFactory eventFactory){
        return new GameController(gameEngine, eventFactory);
    }

    @Bean
    public GameEngine gameEngine(GameConfig config, NumberGenerator numGenerator) {
        return new SimpleGameEngine(config, numGenerator);
    }

    @Bean
    public EventFactory eventAdapter(){
        return new EventFactoryImpl();
    }

    @Bean
    public NumberGenerator numberGenerator(){
        return new RandomNumberGenerator();
    }
}
