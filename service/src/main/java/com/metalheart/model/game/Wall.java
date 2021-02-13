package com.metalheart.model.game;

import com.metalheart.model.common.Material;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;

public class Wall extends GameObject {

    public Wall(long id, Polygon2d shape, Material material, Vector2d pos) {
        super(id, shape, material, pos);
    }

    @Override
    public Wall clone() {
        var cloned = new Wall(getId(), getShape(), getMaterial(), getPos());
        return cloned;
    }
    @Override
    public GameObjectType getType() {
        return GameObjectType.WALL;
    }
}
