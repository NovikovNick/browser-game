package com.metalheart.showcase.service;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface CanvasService {

    GraphicsContext getGraphicsContext();

    Scene createScene();

    void clear();

    Vector2d getCenter();

    void draw(Vector2d p, Color color);

    void draw(Vector2d p0, Vector2d p1, Color color);

    void drawArrow(Vector2d from, Vector2d to, Color color);

    void draw(Polygon2d p, Color color);
}
