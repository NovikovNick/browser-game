package com.metalheart.service.physic;

import com.metalheart.model.common.CollisionPair;
import com.metalheart.model.game.GameObject;
import java.util.Collection;
import java.util.Set;

public interface BroadPhaseAlgorithm {

    Set<CollisionPair> findPairs(Collection<GameObject> bodies);
}
