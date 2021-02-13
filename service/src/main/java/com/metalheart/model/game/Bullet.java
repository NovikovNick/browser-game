package com.metalheart.model.game;

import com.metalheart.model.common.Material;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bullet extends GameObject {

    private Long playerId;
    private Instant createdAt;

    public Bullet(long id, Polygon2d shape, Material material, Vector2d pos) {
        super(id, shape, material, pos);
    }

    @Override
    public Bullet clone() {
        var cloned = new Bullet(getId(), getShape(), getMaterial(), getPos());
        cloned.setPlayerId(playerId);
        cloned.setCreatedAt(createdAt);
        return cloned;
    }

    @Override
    public GameObjectType getType() {
        return GameObjectType.BULLET;
    }
}
