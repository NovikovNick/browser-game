package com.metalheart.model.common;

import com.metalheart.model.game.GameObject;
import lombok.Data;

@Data
public class Manifold {
    private final GameObject a, b;
    private final Vector2d normal;
    private final float penetration;
}
