package com.metalheart.showcase.service.impl;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.showcase.service.CanvasService;
import com.metalheart.showcase.service.ShowcaseInputService;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.springframework.stereotype.Service;

@Service
public class CanvasServiceImpl implements CanvasService {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 700;

    private final ShowcaseInputService inputService;

    private Canvas game;

    public CanvasServiceImpl(ShowcaseInputService showcaseInputService) {
        this.inputService = showcaseInputService;
    }

    @Override
    public Scene createScene() {

        game = new Canvas(WIDTH, HEIGHT);

        StackPane root = new StackPane();
        root.getChildren().addAll(game);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(inputService.getKeyPressHandler());
        scene.setOnKeyReleased(inputService.getKeyReleaseHandler());
        scene.setOnMouseClicked(inputService.getMouseClicked());
        return scene;
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return game.getGraphicsContext2D();
    }

    @Override
    public void clear() {
        GraphicsContext gc = getGraphicsContext();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }

    @Override
    public Vector2d getCenter() {
        return Vector2d.of(WIDTH / 2, HEIGHT / 2);
    }

    @Override
    public void draw(Vector2d p, Color color) {

        Vector2d point = p;

        GraphicsContext gc = getGraphicsContext();
        gc.setFill(color);
        gc.fillOval(
            point.getD0() - 3,
            point.getD1() - 3,
            6,
            6
        );
    }

    @Override
    public void draw(Vector2d p0, Vector2d p1, Color color) {


        GraphicsContext gc = getGraphicsContext();

        gc.setStroke(color);
        gc.strokeLine(p0.getD0(), p0.getD1(), p1.getD0(), p1.getD1());

        draw(p0, color);
        draw(p1, color);
    }

    @Override
    public void drawArrow(Vector2d from, Vector2d to, Color color) {

        GraphicsContext gc = getGraphicsContext();

        gc.setStroke(color);
        gc.strokeLine(
            from.getD0(), from.getD1(),
            to.getD0(), to.getD1()
        );

        draw(from, color);
    }

    @Override
    public void draw(Polygon2d p, Color color) {

        List<Vector2d> points = p.getPoints();
        for (int i = 0; i < points.size(); i++) {
            int next = i == points.size() - 1 ? 0 : i + 1;
            draw(points.get(i), points.get(next), color);
        }
    }
}
