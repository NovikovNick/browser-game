package com.metalheart.service.impl;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.service.ShapeService;
import org.springframework.stereotype.Service;

@Service
public class ShapeServiceImpl implements ShapeService {


    public static final Polygon2d PLAYER_BOUNDING_BOX = new Polygon2d(
        new Vector2d(-50, -50),
        new Vector2d(50, -50),
        new Vector2d(50, 50),
        new Vector2d(-50, 50)
    );

    public static final Polygon2d WALL_BOUNDING_BOX = new Polygon2d(
        new Vector2d(-50, -50),
        new Vector2d(100, -50),
        new Vector2d(100, 50),
        new Vector2d(-50, 50)
    );

    public static final Polygon2d BULLET_BOUNDING_BOX = new Polygon2d(
        Vector2d.of(0, 0),
        Vector2d.of(0, 5),
        Vector2d.of(10, 5),
        Vector2d.of(10, 0)
    );

    @Override
    public Polygon2d playerBoundingBox() {
        return PLAYER_BOUNDING_BOX;
    }

    @Override
    public Polygon2d wallBoundingBox() {
        return WALL_BOUNDING_BOX;
    }

    @Override
    public Polygon2d bulletBoundingBox() {
        return BULLET_BOUNDING_BOX;
    }
}
