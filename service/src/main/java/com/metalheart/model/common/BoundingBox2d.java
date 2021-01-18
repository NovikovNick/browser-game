package com.metalheart.model.common;

import lombok.Data;

@Data
public class BoundingBox2d {

    private final Vector2d pointMin;
    private final Vector2d pointMax;

    public static BoundingBox2d of(Vector2d... points) {

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

        return new BoundingBox2d(Vector2d.of(d0Min, d1Min), Vector2d.of(d0Max, d1Max));
    }

    public Vector2d getCenter() {
        return Vector2d.of(
            pointMin.getD0() + (pointMax.getD0() - pointMin.getD0()) / 2,
            pointMin.getD1() + (pointMax.getD1() - pointMin.getD1()) / 2
        );
    };
}
