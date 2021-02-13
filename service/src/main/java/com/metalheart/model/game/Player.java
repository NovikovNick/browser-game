package com.metalheart.model.game;

import com.metalheart.model.common.Material;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends GameObject {

    private String sessionId;
    private String username;

    public Player(long id, Polygon2d shape, Material material, Vector2d pos) {
        super(id, shape, material, pos);
    }

    @Override
    public Player clone() {
        var cloned = new Player(getId(), getShape(), getMaterial(), getPos());
        cloned.setSessionId(sessionId);
        cloned.setUsername(username);
        return cloned;
    }

    @Override
    public GameObjectType getType() {
        return GameObjectType.PLAYER;
    }
}
