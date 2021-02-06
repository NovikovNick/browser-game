package com.metalheart.service.tmp.impl;

import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Line;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.tmp.GameObject;
import com.metalheart.service.tmp.BroadPhaseAlgorithm;
import com.metalheart.service.tmp.CollisionPair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SweepAndPruneAlgorithm implements BroadPhaseAlgorithm {

    @Override
    public Set<CollisionPair> findPairs(Collection<GameObject> bodies) {

        Set<CollisionPair> res = new HashSet<>();

        List<Entry> entries = bodies.stream().map(Entry::new).collect(Collectors.toList());

        int axis = findHighestVarianceAxis(entries);

        Entry[] sorted = entries.toArray(new Entry[0]);
        Arrays.sort(sorted, comparingAxis(axis));

        for (int i = 0; i < sorted.length; i++) {

            Line p1 = GeometryUtil.getProjection(sorted[i].aabb, axis);

            for (int j = i + 1; j < sorted.length; j++) {

                Line p2 = GeometryUtil.getProjection(sorted[j].aabb, axis);

                if (p1.getEnd() >= p2.getStart()) {

                    if (sorted[i].body.getMass() == 0 && sorted[j].body.getMass() == 0) {
                        continue;
                    }

                    GameObject fst = sorted[sorted[i].body.getMass() != 0 ? i : j].body;
                    GameObject snd = sorted[sorted[i].body.getMass() != 0 ? j : i].body;

                    res.add(new CollisionPair(fst, snd));

                } else {
                    break;
                }
            }
        }
        return res;
    }


    private int findHighestVarianceAxis(List<Entry> entries) {

        if (entries.isEmpty()) {
            return 0;
        }
        Entry fstEntry = entries.get(0);

        float maxD0 = fstEntry.aabb.getMax().getD0();
        float maxD1 = fstEntry.aabb.getMax().getD1();
        float minD0 = fstEntry.aabb.getMin().getD0();
        float minD1 = fstEntry.aabb.getMin().getD1();

        for (int i = 1; i < entries.size(); i++) {
            Entry entry = entries.get(i);

            maxD0 = Math.max(maxD0, entry.aabb.getMax().getD0());
            maxD1 = Math.max(maxD1, entry.aabb.getMax().getD1());
            minD0 = Math.min(minD0, entry.aabb.getMin().getD0());
            minD1 = Math.min(minD1, entry.aabb.getMin().getD1());
        }
        return (maxD0 - minD0) > (maxD1 - minD1) ? 0 : 1;
    }

    private static Comparator<Entry> comparingAxis(int axis) {
        return Comparator.comparingDouble(entry -> GeometryUtil.getProjection(entry.aabb, axis).getStart());
    }


    private static class Entry {

        final GameObject body;
        final AABB2d aabb;

        private Entry(GameObject body) {
            this.body = body;
            this.aabb = AABB2d.of(body.getShape().getPoints());
        }
    }
}
