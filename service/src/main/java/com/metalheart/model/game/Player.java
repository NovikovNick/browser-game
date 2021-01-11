package com.metalheart.model.game;

import com.metalheart.model.common.Vector2d;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"sessionId", "id"})
public class Player {

    private String sessionId;
    private String username;
    private String id;

    private Vector2d mousePos;
    private GameObject gameObject;
}
