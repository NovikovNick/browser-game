package com.metalheart.showcase;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Material;
import com.metalheart.service.state.CollisionDetectionService;
import com.metalheart.service.state.ShapeService;
import com.metalheart.showcase.service.CanvasService;
import com.metalheart.showcase.service.ShowcaseInputService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class Showcase extends AnimationTimer {

    public static final int SPEED = 5;
    public static final float GRAVITY  = 1;
    private final CanvasService canvasService;
    private final ShowcaseInputService inputService;
    private final ShapeService shapeService;
    private final CollisionDetectionService collisionDetectionService;

    private Long previousAnimationAt;

    private Body incBody;
    private List<Body> bodies;

    public Showcase(CanvasService canvasService,
                    ShowcaseInputService inputService,
                    ShapeService shapeService,
                    CollisionDetectionService collisionDetectionService) {
        this.canvasService = canvasService;
        this.inputService = inputService;
        this.shapeService = shapeService;
        this.collisionDetectionService = collisionDetectionService;
        incBody = new Body(shapeService.playerShape(), Vector2d.of(100, 100), Material.WOOD);
        int sizeX = 1024;
        int sizeY = 768;
        int width = 100;
        Polygon2d vert = new Polygon2d(
            Vector2d.of(0 - width, 0),
            Vector2d.of(0 - width, sizeY),
            Vector2d.of(0, sizeY),
            Vector2d.of(0, 0)
        );

        bodies = new ArrayList<>();
        bodies.addAll(asList(
            incBody,
            new Body(shapeService.bulletShape(),  Vector2d.of(200, 400), Material.METAL),
            new Body(shapeService.wallShape(),  Vector2d.of(400, 400), Material.METAL),
            new Body(vert,  Vector2d.of(0, 0), Material.STATIC),
            new Body(vert,  Vector2d.of(sizeX + width, 0), Material.STATIC),
            new Body(new Polygon2d(
                Vector2d.of(15.0f, 724.0f),
                Vector2d.of(13.0f, 758.0f),
                Vector2d.of(1005.0f, 755.0f),
                Vector2d.of(1005.0f, 733.0f)
            ), Vector2d.of(0, 0), Material.STATIC)
        ));
    }

    @Override
    public void handle(long now) {

        float dt = getDeltaTime(now);
        Vector2d center = canvasService.getCenter();
        Vector2d mousePos = inputService.getMousePosition();

        PlayerInput input = inputService.getInput();
        incBody.force = incBody.force.plus(getInputVector(input).scale(SPEED));

        if (input.getLeftBtnClicked()) {
            Body body = new Body(new Polygon2d(
                Vector2d.of(0, 0),
                Vector2d.of(0, 30),
                Vector2d.of(30, 30),
                Vector2d.of(30, 0)
            ), mousePos, Material.BOUNCY_BALL);
            bodies.add(body);
        }

        Polygon2d[] rects = bodies.stream()
            .map(body -> {
                if (body.mass != 0) {
                    body.force = body.force.plus(Vector2d.UNIT_VECTOR_D1.scale(GRAVITY));
                }
                body.integrate(1);
                return body.getShape();
            })
            .collect(Collectors.toList())
            .toArray(new Polygon2d[0]);

        int unit = 5;
        for (int i = 0; i < rects.length; i++) {

            for (int j = 0; j < rects.length; j++) {

                if(i == j) {
                    continue;
                }
                CollisionResult collisionResult = collisionDetectionService.detectCollision(rects[i], rects[j]);
                if (collisionResult.isCollide()) {
                    resolveCollision(bodies.get(i), bodies.get(j), collisionResult.getNormal());
                }
            }
        }

        canvasService.clear();
        for (int i = 0; i < rects.length; i++) {
            Body body = bodies.get(i);
            Vector2d velocity = body.velocity;
            Vector2d c = AABB2d.of(body.getShape().getPoints()).getCenter();
            canvasService.drawArrow(c, c.plus(velocity.scale(unit)), Color.RED);
            canvasService.draw(body.getShape(), Color.WHITE);
        }
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
        float e = Math.min(a.material.getRestitution(), b.material.getRestitution());

        // Вычисляем скаляр импульса силы
        float j = -(1 + e) * velAlongNormal;
        j /= a.invMass + b.invMass;

        // Прикладываем импульс силы
        Vector2d impulse = normal.scale(j);
        a.velocity = a.velocity.plus(impulse.scale(a.invMass).reversed());
        b.velocity = b.velocity.plus(impulse.scale(b.invMass));


        /*
        float massSum = a.mass + b.mass;
        float ratio = a.mass / massSum;
        a.velocity = a.velocity.plus(impulse.scale(ratio).reversed());
        b.velocity = b.velocity.plus(impulse.scale(ratio));
        */
    }

    private Vector2d getInputVector(PlayerInput input) {

        Vector2d direction = Vector2d.ZERO_VECTOR;
        if (input.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
        if (input.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
        if (input.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
        if (input.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);

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
        private final Material material;

        private final float mass;
        private final float invMass;

        private Vector2d force;
        private Vector2d velocity;

        private Vector2d pos;
        private float rot;

        private Body(Polygon2d shape, Vector2d pos, Material material) {

            this.shape = shape;
            this.material = material;

            this.mass = calculateMass(shape, material);
            this.invMass = this.mass == 0 ? 0 : 1f / this.mass;

            this.force = Vector2d.ZERO_VECTOR;
            this.velocity = Vector2d.ZERO_VECTOR;

            this.pos = pos;
            this.rot = 0;
        }

        public void integrate(float dt) {
            velocity = velocity.plus(force.scale(invMass * dt));
            pos = pos.plus(velocity);
            force = Vector2d.ZERO_VECTOR;
        }

        public Polygon2d getShape() {
            return shape.withOffset(pos);
        }

        private float calculateMass(Polygon2d shape, Material material) {
            float area = 50; // todo: calculate from shape
            return material.getDensity() * area;
        }
    }
}
