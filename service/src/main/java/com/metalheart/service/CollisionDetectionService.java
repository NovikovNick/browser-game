package com.metalheart.service;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Line;

public interface CollisionDetectionService {
    CollisionResult detectCollision(Line a, Line b);
}
