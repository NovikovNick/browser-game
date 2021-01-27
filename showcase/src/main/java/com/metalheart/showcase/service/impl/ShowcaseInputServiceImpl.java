package com.metalheart.showcase.service.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.common.Vector2d;
import com.metalheart.showcase.service.ShowcaseInputService;
import java.awt.MouseInfo;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import org.springframework.stereotype.Service;

@Service
public class ShowcaseInputServiceImpl implements ShowcaseInputService {

    private boolean wPressed;
    private boolean aPressed;
    private boolean sPressed;
    private boolean dPressed;


    private EventHandler<KeyEvent> keyPressHandler = e -> {
        switch (e.getCode()) {
            case W:
                wPressed = true;
                break;
            case A:
                aPressed = true;
                break;
            case S:
                sPressed = true;
                break;
            case D:
                dPressed = true;
                break;
        }
    };


    private EventHandler<KeyEvent> keyReleaseHandler = e -> {
        switch (e.getCode()) {
            case W:
                wPressed = false;
                break;
            case A:
                aPressed = false;
                break;
            case S:
                sPressed = false;
                break;
            case D:
                dPressed = false;
                break;
        }
    };

    @Override
    public EventHandler<? super KeyEvent> getKeyPressHandler() {
        return keyPressHandler;
    }

    @Override
    public EventHandler<? super KeyEvent> getKeyReleaseHandler() {
        return keyReleaseHandler;
    }

    @Override
    public Vector2d getMousePosition() {
        return Vector2d.of(
                MouseInfo.getPointerInfo().getLocation().x,
                MouseInfo.getPointerInfo().getLocation().y);
    }

    @Override
    public PlayerInput getInput() {

        PlayerInput input = new PlayerInput();
        input.setIsPressedW(wPressed);
        input.setIsPressedA(aPressed);
        input.setIsPressedS(sPressed);
        input.setIsPressedD(dPressed);

        return input;
    }

}
