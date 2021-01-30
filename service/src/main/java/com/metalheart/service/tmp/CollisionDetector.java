package com.metalheart.service.tmp;

import java.util.Set;

public interface CollisionDetector {

    Set<Manifold> findCollision(Iterable<Body> bodies);
}
