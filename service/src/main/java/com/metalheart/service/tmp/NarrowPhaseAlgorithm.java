package com.metalheart.service.tmp;

import com.metalheart.model.common.CollisionResult;

public interface NarrowPhaseAlgorithm {

    CollisionResult findCollision(CollisionPair pair);
}
