package com.metalheart.service;

import com.metalheart.model.common.Polygon2d;

public interface ShapeService {

    Polygon2d playerShape();

    Polygon2d wallShape();

    Polygon2d bulletShape();
}
