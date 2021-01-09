package com.metalheart.model.game;

import com.metalheart.model.common.Polygon2d;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RigidBody {

    private final Polygon2d shape;
}
