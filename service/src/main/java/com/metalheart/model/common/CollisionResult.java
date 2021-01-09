package com.metalheart.model.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollisionResult {

    private final boolean collide;

    private final float depth;
    private final Vector2d normal;

    private Vector2d p1;
    private Vector2d p2;
    private boolean sign;
}
