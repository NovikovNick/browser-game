package com.metalheart.model.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameObject {

    private final Transform transform;
    private final RigidBody rigidBody;
}
