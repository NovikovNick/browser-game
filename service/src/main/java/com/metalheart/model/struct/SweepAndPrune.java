package com.metalheart.model.struct;

import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Line;
import com.metalheart.model.game.GameObject;
import com.metalheart.service.GeometryUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class SweepAndPrune {

    private static final int AXIS = 0;

    public static void collide(Iterable<GameObject> data, BiConsumer<GameObject, GameObject> onCollide) {

        List<GameObject> list = new ArrayList<>();
        data.forEach(list::add);
        Collections.sort(list, comparingAxis(AXIS));
        GameObject[] objs = list.toArray(new GameObject[0]);

        for (int i = 0; i < objs.length; i++) {

            AABB2d aabb1 = AABB2d.of(objs[i].getRigidBody().getShape().getPoints());
            Line p1 = GeometryUtil.getProjection(aabb1, AXIS);

            for (int j = i + 1; j < objs.length; j++) {

                AABB2d aabb2 = AABB2d.of(objs[j].getRigidBody().getShape().getPoints());
                Line p2 = GeometryUtil.getProjection(aabb2, AXIS);

                if (p1.getEnd() < p2.getStart()) {
                    break;
                } else {
                    onCollide.accept(objs[i], objs[j]);
                }
            }
        }
    }

    private static Comparator<GameObject> comparingAxis(int axis) {
        return Comparator.comparingDouble(obj -> {
            AABB2d aabb = AABB2d.of(obj.getRigidBody().getShape().getPoints());
            return GeometryUtil.getProjection(aabb, axis).getStart();
        });
    }

}
