package com.metalheart.model.game;

import com.metalheart.model.common.Vector2d;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {

    private String sessionId;
    private String username;

    private Vector2d mousePos;
    private GameObject gameObject;
}
