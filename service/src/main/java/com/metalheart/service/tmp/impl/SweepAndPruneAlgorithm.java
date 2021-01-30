package com.metalheart.service.tmp.impl;

import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Line;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.tmp.Body;
import com.metalheart.service.tmp.BroadPhaseAlgorithm;
import com.metalheart.service.tmp.CollisionPair;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;

@Service
public class SweepAndPruneAlgorithm implements BroadPhaseAlgorithm {

    private static final int AXIS = 0;// todo sort by 2 axis

    @Override
    public Set<CollisionPair> findPairs(Iterable<Body> bodies) {

        Set<CollisionPair> res = new HashSet<>();

        TreeSet<Body> sorted = new TreeSet<>(comparingAxis(AXIS));
        for (Body body : bodies) {
            sorted.add(body);
        }
        int x = 0;
        Body[] sortedArr = sorted.toArray(new Body[0]);
        for (int i = 0; i < sortedArr.length; i++) {

            AABB2d aabb1 = AABB2d.of(sortedArr[i].getShape().getPoints());
            Line p1 = GeometryUtil.getProjection(aabb1, AXIS);

            for (int j = i + 1; j < sortedArr.length; j++) {

                AABB2d aabb2 = AABB2d.of(sortedArr[j].getShape().getPoints());
                Line p2 = GeometryUtil.getProjection(aabb2, AXIS);
                x++;
                if (p1.getEnd() < p2.getStart()) {
                    break;
                } else {

                    Body fst = sortedArr[sortedArr[i].getMass() != 0 ? i : j];
                    Body snd = sortedArr[sortedArr[i].getMass() != 0 ? j : i];

                    res.add(new CollisionPair(fst, snd));
                }
            }
        }
        return res;
    }

    private static Comparator<Body> comparingAxis(int axis) {
        return Comparator.comparingDouble(obj -> {
            AABB2d aabb = AABB2d.of(obj.getShape().getPoints());
            return GeometryUtil.getProjection(aabb, axis).getStart();
        });
    }
}
