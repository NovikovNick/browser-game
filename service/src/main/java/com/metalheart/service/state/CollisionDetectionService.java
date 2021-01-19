package com.metalheart.service.state;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Line;
import com.metalheart.model.common.Polygon2d;

public interface CollisionDetectionService {
    CollisionResult detectCollision(Line a, Line b);

    CollisionResult detectCollision(Polygon2d a, Polygon2d b);
}
