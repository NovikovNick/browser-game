package com.metalheart.service.tmp;

import java.util.Collection;
import java.util.Set;

public interface CollisionDetector {

    Set<Manifold> findCollision(Collection<Body> bodies);
}
