package com.metalheart.service.tmp;

import com.metalheart.model.common.Vector2d;
import lombok.Data;

@Data
public class Manifold {
    private final GameObject a, b;
    private final Vector2d normal;
    private final float penetration;
}
