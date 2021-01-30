package com.metalheart.service.tmp;

import java.util.Set;

public interface BroadPhaseAlgorithm {

    Set<CollisionPair> findPairs(Iterable<Body> bodies);
}
