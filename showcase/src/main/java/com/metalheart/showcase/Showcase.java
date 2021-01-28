package com.metalheart.showcase;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.service.GeometryUtil;
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

    public static final int SPEED = 1;
    private final CanvasService canvasService;
    private final ShowcaseInputService inputService;
    private final ShapeService shapeService;
    private final CollisionDetectionService collisionDetectionService;

    private Long previousAnimationAt;

    private Body incBody;
    private Body refBody;

    public Showcase(CanvasService canvasService,
                    ShowcaseInputService inputService,
                    ShapeService shapeService,
                    CollisionDetectionService collisionDetectionService) {
        this.canvasService = canvasService;
        this.inputService = inputService;
        this.shapeService = shapeService;
        this.collisionDetectionService = collisionDetectionService;
        incBody = new Body(shapeService.playerShape(), Vector2d.of(100, 100), 1);
        refBody = new Body(shapeService.wallShape(), canvasService.getCenter(), 1);
    }

    @Override
    public void handle(long now) {

        float dt = getDeltaTime(now);
        Vector2d center = canvasService.getCenter();
        Vector2d mousePos = inputService.getMousePosition();

        incBody.velocity = incBody.velocity.plus(getInputVector());

        Polygon2d inc = incBody.integrate(1);
        Polygon2d ref = refBody.integrate(1);
        CollisionResult collisionResult = collisionDetectionService.detectCollision(inc, ref);
        if (collisionResult.isCollide()) {
            resolveCollision(incBody, refBody, collisionResult.getNormal());
        }

        // draw

        GraphicsContext gc = canvasService.getGraphicsContext();
        canvasService.clear();

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        canvasService.draw(inc, Color.BLUE);
        canvasService.draw(ref, Color.WHITE);
    }

    void resolveCollision( Body a, Body b, Vector2d normal) {
        // Вычисляем относительную скорость
        Vector2d rv = b.velocity.plus(a.velocity.reversed());

        // Вычисляем относительную скорость относительно направления нормали
        float velAlongNormal = rv.dotProduct(normal);

        // Не выполняем вычислений, если скорости разделены
        if(velAlongNormal > 0)
            return;

        // Вычисляем упругость
        float e = 1;// min( A.restitution, B.restitution)

        // Вычисляем скаляр импульса силы
        float j = -(1 + e) * velAlongNormal;
        j /= a.invMass + b.invMass;

        // Прикладываем импульс силы
        Vector2d impulse = normal.scale(j);
        a.velocity = a.velocity.plus(impulse.scale(a.invMass).reversed());
        b.velocity = a.velocity.plus(impulse.scale(a.invMass));
    }

    private Vector2d getInputVector() {

        PlayerInput req = inputService.getInput();

        Vector2d direction = Vector2d.ZERO_VECTOR;
        if (req.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
        if (req.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
        if (req.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
        if (req.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);

        return direction;
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


    private static class Body {

        private final Polygon2d shape;
        private final int invMass;
        private Vector2d velocity;
        private Vector2d pos;
        private float rot;

        private Body(Polygon2d shape, Vector2d pos, int mass) {
            this.shape = shape;
            this.invMass = 1 / mass;
            this.velocity = Vector2d.ZERO_VECTOR;
            this.pos = pos;
            this.rot = 0;
        }

        public Polygon2d integrate(float dt) {
            pos = pos.plus(velocity).scale(dt);
            return shape.withOffset(pos);
        }
    }
}
