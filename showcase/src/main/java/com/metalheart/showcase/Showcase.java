package com.metalheart.showcase;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.service.state.CollisionDetectionService;
import com.metalheart.service.state.ShapeService;
import com.metalheart.showcase.service.CanvasService;
import com.metalheart.showcase.service.ShowcaseInputService;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

@Component
public class Showcase extends AnimationTimer {

    private final CanvasService canvasService;
    private final ShowcaseInputService inputService;
    private final ShapeService shapeService;
    private final CollisionDetectionService collisionDetectionService;


    private Long previousAnimationAt;

    public Showcase(CanvasService canvasService,
                    ShowcaseInputService inputService,
                    ShapeService shapeService,
                    CollisionDetectionService collisionDetectionService) {
        this.canvasService = canvasService;
        this.inputService = inputService;
        this.shapeService = shapeService;
        this.collisionDetectionService = collisionDetectionService;
    }

    @Override
    public void handle(long now) {

        float dt = getDeltaTime(now);
        Vector2d center = canvasService.getCenter();
        Vector2d point = inputService.getMousePosition();

        GraphicsContext gc = canvasService.getGraphicsContext();
        canvasService.clear();

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        Polygon2d rect = shapeService.playerShape().withOffset(point);
        Polygon2d wall = shapeService.wallShape().withOffset(center);

        canvasService.draw(rect, Color.BLUE);
        canvasService.draw(wall, Color.WHITE);

        CollisionResult collisionResult = collisionDetectionService.detectCollision(rect, wall);

        if (collisionResult.isCollide()) {
            canvasService.draw(collisionResult.getP1(), collisionResult.getP2(), Color.RED);
            System.out.println(collisionResult.getNormal() + " -> " + collisionResult.getDepth());
        }
    }

    private float getDeltaTime(long now) {
        float timeDelta;
        if (previousAnimationAt == null) {
            timeDelta = 0.0015f;
        } else {
            timeDelta = (now - previousAnimationAt) / 1000000000f;
        }
        previousAnimationAt = now;
        return timeDelta;
    }
}
