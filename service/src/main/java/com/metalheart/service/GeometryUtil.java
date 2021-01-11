package com.metalheart.service;

import com.metalheart.model.common.Line;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import java.util.stream.Collectors;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GeometryUtil {

    private GeometryUtil() {
        throw new UnsupportedOperationException();
    }

    public static Line getProjection(Polygon2d polygon) {

        float initialValue = polygon.getPoints().get(0).getD0() ;
        float min = initialValue;
        float max = initialValue;

        for (Vector2d point : polygon.getPoints()) {
            float dim = point.getD0();
            max = dim > max ? dim : max;
            min = dim < min ? dim : min;
        }
        return new Line(min, max);
    }

    /* ROTATION */

    public static float getAngleRadian(Vector2d p1, Vector2d p2) {
        float deltaX = p2.getD0() - p1.getD0();
        float deltaY = p2.getD1() - p1.getD1();
        return (float) Math.atan2(deltaY, deltaX);
    }

    public static Vector2d rotate(Vector2d p, float angleRadian) {
        float cos = (float) cos(angleRadian);
        float sin = (float) sin(angleRadian);

        float x = p.getD0();
        float y = p.getD1();

        return new Vector2d(
            x * cos - y * sin,
            x * sin + y * cos);
    }

    public static Vector2d rotate(Vector2d p, float angleRadian, Vector2d center) {
        float cos = (float) cos(angleRadian);
        float sin = (float) sin(angleRadian);

        float x = p.getD0();
        float y = p.getD1();

        float x0 = center.getD0();
        float y0 = center.getD1();

        return new Vector2d(
            x0 + (x - x0) * cos - (y - y0) * sin,
            y0 + (y - y0) * cos + (x - x0) * sin);
    }

    public static Polygon2d rotate(Polygon2d polygon, float radian) {

        return new Polygon2d(polygon.getPoints().stream()
            .map(p -> rotate(p, radian))
            .collect(Collectors.toList()));
    }

    public static Polygon2d rotate(Polygon2d polygon, float angleRadian, Vector2d center) {

        return new Polygon2d(polygon.getPoints().stream()
            .map(p -> rotate(p, angleRadian, center))
            .collect(Collectors.toList()));
    }
}
