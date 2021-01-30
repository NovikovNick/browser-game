package com.metalheart.showcase;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Material;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.state.ShapeService;
import com.metalheart.service.tmp.Body;
import com.metalheart.service.tmp.CollisionDetector;
import com.metalheart.service.tmp.CollisionResolver;
import com.metalheart.service.tmp.Manifold;
import com.metalheart.showcase.service.CanvasService;
import com.metalheart.showcase.service.ShowcaseInputService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class Showcase extends AnimationTimer {

    public static final int SPEED = 100;
    public static final float GRAVITY  = 9;
    public static final int VECTOR_UNIT = 5;
    public static final Polygon2d WALL_1 = new Polygon2d(
        Vector2d.of(1902.0f, 753.0f),
        Vector2d.of(1856.0f, 758.0f),
        Vector2d.of(1856.0f, 5.0f),
        Vector2d.of(1900.0f, 3.0f)
    );
    public static final Polygon2d WALL_2 = new Polygon2d(
        Vector2d.of(5.0f, 757.0f),
        Vector2d.of(29.0f, 758.0f),
        Vector2d.of(27.0f, 6.0f),
        Vector2d.of(8.0f, 8.0f)
    );
    public static final Polygon2d WALL_3 = new Polygon2d(
        Vector2d.of(1841.0f, 759.0f),
        Vector2d.of(1839.0f, 719.0f),
        Vector2d.of(40.0f, 721.0f),
        Vector2d.of(44.0f, 758.0f)
    );
    public static final Polygon2d WALL_4 = new Polygon2d(
        Vector2d.of(34.0f, 2.0f),
        Vector2d.of(38.0f, 32.0f),
        Vector2d.of(1844.0f, 35.0f),
        Vector2d.of(1842.0f, 6.0f)
    );

    public static final Polygon2d ARROW = new Polygon2d(
        Vector2d.of(0, 0),
        Vector2d.of(0, 5),
        Vector2d.of(100, 5),
        Vector2d.of(100, 0)
    );

    private final CanvasService canvasService;
    private final ShowcaseInputService inputService;
    private final ShapeService shapeService;

    private final CollisionResolver collisionResolver;
    private final CollisionDetector collisionDetector;

    private Long previousAnimationAt;

    private Body incBody;
    private List<Body> bodies;

    private final AtomicLong bodiesSq;

    public Showcase(CanvasService canvasService,
                    ShowcaseInputService inputService,
                    ShapeService shapeService,
                    CollisionResolver collisionResolver,
                    CollisionDetector collisionDetector) {

        this.canvasService = canvasService;
        this.inputService = inputService;
        this.shapeService = shapeService;
        this.collisionResolver = collisionResolver;
        this.collisionDetector = collisionDetector;

        bodiesSq = new AtomicLong(0);

        incBody = new Body(
            bodiesSq.incrementAndGet(),
            shapeService.playerShape(),
            Material.WOOD,
            canvasService.getCenter());

        bodies = new ArrayList<>();
        bodies.addAll(asList(
            incBody,
            new Body(bodiesSq.incrementAndGet(),  WALL_1, Material.STATIC, Vector2d.ZERO_VECTOR),
            new Body(bodiesSq.incrementAndGet(),  WALL_2, Material.STATIC, Vector2d.ZERO_VECTOR),
            new Body(bodiesSq.incrementAndGet(),  WALL_3, Material.STATIC, Vector2d.ZERO_VECTOR),
            new Body(bodiesSq.incrementAndGet(),  WALL_4, Material.STATIC, Vector2d.ZERO_VECTOR)
        ));
    }

    @Override
    public void handle(long now) {

        float dt = getDeltaTime(now);
        PlayerInput input = inputService.getInput();
        Vector2d mousePos = inputService.getMousePosition();

        // apply forces
        incBody.setForce(incBody.getForce().plus(getInputVector(input).scale(SPEED)));
        if (input.getLeftBtnClicked()) {

            Vector2d start = incBody.getPos();
            Vector2d end = mousePos;
            float angleRadian = GeometryUtil.getAngleRadian(start, end);
            Vector2d dir = GeometryUtil.rotate(Vector2d.UNIT_VECTOR_D0, angleRadian, Vector2d.ZERO_VECTOR);
            Body bullet = new Body(
                bodiesSq.incrementAndGet(),
                ARROW,
                Material.SUPER_BALL,
                start.plus(dir.scale(100)));
            bullet.setForce(dir.normalize().scale(SPEED * 50));
            bodies.add(bullet);
        }

        // integrate
        for (Body body : bodies) {
            if (body.getMass() != 0) {
                body.setForce(body.getForce().plus(Vector2d.UNIT_VECTOR_D1.scale(GRAVITY)));
            }
            body.setVelocity(body.getVelocity().plus(body.getForce().scale(body.getInvMass() * dt)));
            body.setPos(body.getPos().plus(body.getVelocity()));
            body.setForce(Vector2d.ZERO_VECTOR);
        }

        // resolve collision
        Set<Manifold> manifolds = collisionDetector.findCollision(bodies);
        collisionResolver.resolve(manifolds);

        // draw
        canvasService.clear();
        for (Body body : bodies) {
            Vector2d velocity = body.getVelocity();
            Vector2d c = AABB2d.of(body.getShape().getPoints()).getCenter();
            canvasService.drawArrow(c, c.plus(velocity.scale(VECTOR_UNIT)), Color.RED);
            canvasService.draw(body.getShape(), Color.WHITE);
        }
    }

    private Vector2d getInputVector(PlayerInput input) {

        Vector2d direction = Vector2d.ZERO_VECTOR;
        if (input.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
        if (input.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
        if (input.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
        if (input.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);

        return direction.normalize();
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
