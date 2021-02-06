package com.metalheart.service.tmp;

import java.util.Collection;
import java.util.Set;

public interface BroadPhaseAlgorithm {

    Set<CollisionPair> findPairs(Collection<GameObject> bodies);
}
