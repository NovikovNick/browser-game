package com.metalheart.service;

import com.metalheart.model.common.Polygon2d;

public interface ShapeService {

    Polygon2d playerBoundingBox();

    Polygon2d bulletBoundingBox();
}
