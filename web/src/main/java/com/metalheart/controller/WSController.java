package com.metalheart.controller;

import com.metalheart.model.Constant;
import com.metalheart.model.InputRequest;
import com.metalheart.model.PlayerInput;
import com.metalheart.service.input.PlayerInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import static com.metalheart.config.WebModuleConfiguration.WEB_CONVERSION_SERVICE;

@Slf4j
@Controller
public class WSController {

    private PlayerInputService playerInputService;

    private ConversionService conversionService;

    public WSController(@Qualifier(WEB_CONVERSION_SERVICE) ConversionService conversionService,
                        PlayerInputService playerInputService) {
        this.conversionService = conversionService;
        this.playerInputService = playerInputService;
    }

    @MessageMapping(Constant.INPUT_PLAYER_STATE)
    public void messages(@Payload InputRequest input,
                         @Header("simpSessionId") String sessionId) {
        playerInputService.add(sessionId, conversionService.convert(input, PlayerInput.class));
    }
}
