package com.metalheart.socket;

import com.metalheart.model.Constant;
import com.metalheart.model.Player;
import com.metalheart.model.ServerTicEvent;
import com.metalheart.service.GameStateService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class SocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameStateService gameStateService;

    @EventListener
    private void handleGameStateEvent(ServerTicEvent event) {


        Set<Player> players = event.getPlayers().entrySet().stream()
            .map(entry -> Player.builder()
                .id(entry.getKey())
                .x(entry.getValue().getD0())
                .y(entry.getValue().getD1())
                .build())
            .collect(Collectors.toSet());

        messagingTemplate.convertAndSend(Constant.OUTPUT_PLAYER_STATE, players);
    }


    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {

        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        gameStateService.registerPlayer(sessionId);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {

        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        gameStateService.unregisterPlayer(sessionId);
    }
}
