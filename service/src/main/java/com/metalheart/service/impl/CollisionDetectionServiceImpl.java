package com.metalheart.service.impl;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Line;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.service.CollisionDetectionService;
import com.metalheart.service.GeometryUtil;
import java.util.List;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;

@Service
public class CollisionDetectionServiceImpl implements CollisionDetectionService {

    @Override
    public CollisionResult detectCollision(Line a, Line b) {

        boolean sign = false;

        if (a.getStart() > a.getEnd()) {
            a = new Line(a.getEnd(), a.getStart());
        }

        if (b.getStart() > b.getEnd()) {
            b = new Line(b.getEnd(), b.getStart());
        }

        if ((b.getEnd() == a.getStart())
            || (a.getEnd() == b.getStart())
            || (a.getEnd() == b.getEnd())
            || (a.getStart() == b.getStart())) {
            return CollisionResult.builder().collide(true).depth(0).build();
        }

        if (a.getEnd() > b.getEnd()) {
            Line tmp = b;
            b = a;
            a = tmp;
            sign = true;
        }

        if (a.getEnd() > b.getStart()) {
            return CollisionResult.builder().collide(true).depth(a.getEnd() - b.getStart()).sign(sign).build();
        }

        return CollisionResult.builder().collide(false).build();
    }

    @Override
    public CollisionResult detectCollision(Polygon2d a, Polygon2d b) {

        if(a == null || b == null) {
            return CollisionResult.builder().collide(false).build();
        }

        Float depth = null;
        Vector2d normal = null;
        Vector2d c1 = null;
        Vector2d c2 = null;

        for (Polygon2d polygon : asList(a, b)) {
            List<Vector2d> points = polygon.getPoints();
            for (int i = 0; i < points.size(); i++) {

                Vector2d p1 = points.get(i);
                Vector2d p2 = i + 1 == points.size() ? points.get(0) : points.get(i + 1);

                float angle = -GeometryUtil.getAngleRadian(p1, p2);
                Line aProjection = GeometryUtil.getProjection(GeometryUtil.rotate(a, angle));
                Line bProjection = GeometryUtil.getProjection(GeometryUtil.rotate(b, angle));
                CollisionResult collisionResult = detectCollision(aProjection, bProjection);
                if (!collisionResult.isCollide()) {

                    return CollisionResult.builder().collide(false).build();

                } else if (depth == null || depth > collisionResult.getDepth()) {

                    depth = collisionResult.getDepth();
                    c1 = p1;
                    c2 = p2;
                    float deltaX = collisionResult.isSign() ? p1.getD0() - p2.getD0() : p2.getD0() - p1.getD0();
                    float deltaY = collisionResult.isSign() ? p1.getD1() - p2.getD1() : p2.getD1() - p1.getD1();
                    normal = Vector2d.of(deltaX, deltaY).normalize();
                }
            }
        }
        return CollisionResult.builder()
            .p1(c1)
            .p2(c2)
            .collide(true)
            .depth(depth)
            .normal(normal)
            .build();
    }
}
