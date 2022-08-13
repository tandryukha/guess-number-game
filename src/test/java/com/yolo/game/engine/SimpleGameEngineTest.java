package com.yolo.game.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleGameEngineTest {

    @Test
    void shouldNotifyAboutRoundStart() {
        GameEngine gameEngine = new SimpleGameEngine();
        gameEngine.start();
    }

    //todo should notify all online players about round end * Once they connect they're notified when round starts/ends and results even if he didn't bet
    //todo should notify all online players about each round
    //todo should accept bets during round * Game accepts bet only if there is an active round
    //todo should not accept bets between rounds     //todo don't accept players when round not started yet - send message back to them * If there is no active round, user is notified about refused bet
    //todo should notify losers      * * Losers are notified about the loss
    //todo should notify winners      * * Winners are notified with the amount won and ratio to original stake
    //todo should notify all about round stats      * * All players receive a message with a list of winning players: nickname:amount

    //todo should register/unregister players. Once unregistered, bet should be distributed

}