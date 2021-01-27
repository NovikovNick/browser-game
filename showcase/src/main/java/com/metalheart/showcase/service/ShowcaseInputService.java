package com.metalheart.showcase.service;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.common.Vector2d;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public interface ShowcaseInputService {

    EventHandler<? super KeyEvent> getKeyPressHandler();
    EventHandler<? super KeyEvent> getKeyReleaseHandler();

    Vector2d getMousePosition();

    PlayerInput getInput();
}
