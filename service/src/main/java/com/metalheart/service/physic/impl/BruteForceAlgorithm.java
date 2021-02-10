package com.metalheart.service.physic.impl;

import com.metalheart.model.game.GameObject;
import com.metalheart.service.physic.BroadPhaseAlgorithm;
import com.metalheart.model.common.CollisionPair;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//@Service
public class BruteForceAlgorithm implements BroadPhaseAlgorithm {

    @Override
    public Set<CollisionPair> findPairs(Collection<GameObject> bodies) {

        Set<CollisionPair> res = new HashSet<>();
        for (GameObject a : bodies) {
            for (GameObject b : bodies) {
                if (!a.equals(b)) {

                    if (a.getMass() == 0 && a.getMass() == 0) {
                        continue;
                    } else if(a.getMass() != 0 ) {
                        res.add(new CollisionPair(a, b));
                    } else {
                        res.add(new CollisionPair(b, a));
                    }
                }
            }
        }
        return res;
    }
}
