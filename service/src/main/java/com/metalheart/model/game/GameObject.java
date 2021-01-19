package com.metalheart.model.game;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class GameObject {
    private final String id;
    private final Transform transform;
    private final RigidBody rigidBody;
}
