package com.metalheart.socket;

import com.metalheart.model.Constant;
import com.metalheart.model.event.ServerTicEvent;
import com.metalheart.service.GameStateService;
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

    public SocketEventListener(SimpMessagingTemplate messagingTemplate,
                               GameStateService gameStateService) {
        this.messagingTemplate = messagingTemplate;
        this.gameStateService = gameStateService;
    }

    @EventListener
    private void handleGameStateEvent(ServerTicEvent event) {
        messagingTemplate.convertAndSend(Constant.OUTPUT_PLAYER_STATE, event.getSnapshot());
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
