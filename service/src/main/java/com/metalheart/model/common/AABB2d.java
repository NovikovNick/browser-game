package com.metalheart.model.common;

import java.util.Collection;
import lombok.Data;

/**
 * Axis-Aligned Bounding Box
 */
@Data
public class AABB2d {

    private final Vector2d min;
    private final Vector2d max;

    public static AABB2d of(Collection<Vector2d> points) {
        return of(points.toArray(new Vector2d[0]));
    }

    public static AABB2d of(Vector2d... points) {

        float d0Min = points[0].getD0();
        float d0Max = points[0].getD0();

        float d1Min = points[0].getD1();
        float d1Max = points[0].getD1();

        for (int i = 1; i < points.length; i++) {

            float d0 = points[i].getD0();
            float d1 = points[i].getD1();

            d0Min = d0Min > d0 ? d0 : d0Min;
            d0Max = d0Max < d0 ? d0 : d0Max;

            d1Min = d1Min > d1 ? d1 : d1Min;
            d1Max = d1Max < d1 ? d1 : d1Max;
        }

        return new AABB2d(Vector2d.of(d0Min, d1Min), Vector2d.of(d0Max, d1Max));
    }

    public Vector2d getCenter() {
        return Vector2d.of(
            min.getD0() + (max.getD0() - min.getD0()) / 2,
            min.getD1() + (max.getD1() - min.getD1()) / 2
        );
    };
}
