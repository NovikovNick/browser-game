package com.metalheart.socket;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import javax.security.auth.Subject;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        return new Principal() {

            private UUID id = UUID.randomUUID();

            @Override
            public String getName() {
                return id.toString();
            }

            @Override
            public boolean implies(Subject subject) {
                return false;
            }
        };
    }
}