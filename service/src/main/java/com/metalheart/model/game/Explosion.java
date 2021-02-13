package com.metalheart.model.game;

import com.metalheart.model.common.Material;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;

public class Explosion extends GameObject {

    public Explosion(long id, Polygon2d shape, Material material, Vector2d pos) {
        super(id, shape, material, pos);
    }

    @Override
    public GameObjectType getType() {
        return GameObjectType.EXPLOSION;
    }
}
