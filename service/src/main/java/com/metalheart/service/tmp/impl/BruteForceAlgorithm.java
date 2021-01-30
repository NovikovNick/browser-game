package com.metalheart.service.tmp.impl;

import com.metalheart.service.tmp.Body;
import com.metalheart.service.tmp.BroadPhaseAlgorithm;
import com.metalheart.service.tmp.CollisionPair;
import java.util.HashSet;
import java.util.Set;

// @Service
public class BruteForceAlgorithm implements BroadPhaseAlgorithm {

    @Override
    public Set<CollisionPair> findPairs(Iterable<Body> bodies) {

        Set<CollisionPair> res = new HashSet<>();
        for (Body a : bodies) {
            for (Body b : bodies) {
                if (!a.equals(b)) {
                    res.add(new CollisionPair(a, b));
                }
            }
        }
        return res;
    }
}
