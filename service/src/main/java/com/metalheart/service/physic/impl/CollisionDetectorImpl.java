package com.metalheart.service.physic.impl;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.game.GameObject;
import com.metalheart.service.physic.BroadPhaseAlgorithm;
import com.metalheart.service.physic.CollisionDetector;
import com.metalheart.model.common.CollisionPair;
import com.metalheart.model.common.Manifold;
import com.metalheart.service.physic.NarrowPhaseAlgorithm;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollisionDetectorImpl implements CollisionDetector {

    @Autowired
    private final BroadPhaseAlgorithm broadPhaseAlgorithm;

    @Autowired
    private final NarrowPhaseAlgorithm narrowPhaseAlgorithm;

    public CollisionDetectorImpl(BroadPhaseAlgorithm broadPhaseAlgorithm,
                                 NarrowPhaseAlgorithm narrowPhaseAlgorithm) {
        this.broadPhaseAlgorithm = broadPhaseAlgorithm;
        this.narrowPhaseAlgorithm = narrowPhaseAlgorithm;
    }

    @Override
    public Set<Manifold> findCollision(Collection<GameObject> bodies) {

        Set<Manifold> res = new HashSet<>();
        Set<CollisionPair> pairs = broadPhaseAlgorithm.findPairs(bodies);
        for (CollisionPair pair : pairs) {
            CollisionResult collisionResult = narrowPhaseAlgorithm.findCollision(pair);
            if (collisionResult.isCollide()) {

                Manifold manifold = new Manifold(
                    pair.getIncident(),
                    pair.getReference(),
                    collisionResult.getNormal(),
                    collisionResult.getDepth());

                res.add(manifold);
            }
        }
        return res;
    }
}
