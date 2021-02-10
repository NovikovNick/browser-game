package com.metalheart.service.physic;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.CollisionPair;

public interface NarrowPhaseAlgorithm {

    CollisionResult findCollision(CollisionPair pair);
}
