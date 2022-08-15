package com.yolo.game;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {
    @LocalServerPort
    int port;

    @Test
    void socketShouldOpen() throws IOException, WebSocketException {
        WebSocket socket = new WebSocketFactory()
                .createSocket(String.format("http://localhost:%s/echo", port))
                .connect();
        assertTrue(socket.isOpen());
    }
}
