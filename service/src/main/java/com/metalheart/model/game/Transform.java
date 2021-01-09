package com.metalheart.model.game;

import com.metalheart.model.common.Vector2d;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transform {

    private final Vector2d position;
    private final Vector2d rotation;
}
