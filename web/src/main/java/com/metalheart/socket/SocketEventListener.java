package com.metalheart.socket;

import com.metalheart.model.Constant;
import com.metalheart.model.event.ServerTicEvent;
import com.metalheart.model.response.SnapshotView;
import com.metalheart.service.state.GameStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class SocketEventListener {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private final GameStateService gameStateService;

    @Autowired
    private final ConversionService conversionService;

    public SocketEventListener(SimpMessagingTemplate messagingTemplate,
                               GameStateService gameStateService,
                               ConversionService conversionService) {
        this.messagingTemplate = messagingTemplate;
        this.gameStateService = gameStateService;
        this.conversionService = conversionService;
    }

    @EventListener
    private void handleGameStateEvent(ServerTicEvent event) {

        event.getSnapshots().forEach((sessionId, snapshot) -> {
            String id = snapshot.getSnapshot().getCharacter().getId();
            SnapshotView view = conversionService.convert(snapshot, SnapshotView.class);
            messagingTemplate.convertAndSendToUser(id, Constant.OUTPUT_PLAYER_STATE, view);
        });
    }


    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {

        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String id = event.getUser().getName();
        gameStateService.registerPlayer(sessionId, id);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {

        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        gameStateService.unregisterPlayer(sessionId);
    }
}
