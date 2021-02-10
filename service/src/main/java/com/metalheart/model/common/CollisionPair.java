package com.metalheart.model.common;

import com.metalheart.model.game.GameObject;
import lombok.Data;

@Data
public class CollisionPair {
    private final GameObject incident, reference;
}

