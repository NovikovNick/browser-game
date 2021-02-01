package com.metalheart.service.tmp.impl;

import com.metalheart.service.tmp.Body;
import com.metalheart.service.tmp.BroadPhaseAlgorithm;
import com.metalheart.service.tmp.CollisionPair;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

//@Service
public class BruteForceAlgorithm implements BroadPhaseAlgorithm {

    @Override
    public Set<CollisionPair> findPairs(Collection<Body> bodies) {

        Set<CollisionPair> res = new HashSet<>();
        for (Body a : bodies) {
            for (Body b : bodies) {
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
