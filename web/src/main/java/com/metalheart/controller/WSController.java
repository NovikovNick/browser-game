package com.metalheart.controller;

import com.metalheart.model.Constant;
import com.metalheart.model.Point2d;
import com.metalheart.service.GameStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WSController {

    @Autowired
    private GameStateService gameStateService;

    @MessageMapping(Constant.INPUT_PLAYER_STATE)
    public void messages(@Payload String msg,
                         Authentication authentication,
                         @Header("simpSessionId") String sessionId) {

        String[] posStrArr = msg.split("\\.");
        Integer posX = Integer.parseInt(posStrArr[0]);
        Integer posY = Integer.parseInt(posStrArr[1]);

        gameStateService.changePlayerState(sessionId, new Point2d(posX, posY));
    }
}
