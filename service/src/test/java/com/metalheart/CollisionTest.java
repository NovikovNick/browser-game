package com.metalheart;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.model.struct.SweepAndPrune;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

import static java.util.Arrays.asList;

public class CollisionTest {

    public static final Polygon2d BOX;

    static {
        int size = 10;
        BOX = new Polygon2d(
            new Vector2d(-size, -size),
            new Vector2d(size, -size),
            new Vector2d(size, size),
            new Vector2d(-size, size)
        );
    }

    @Test
    public void test() {

        // arrange
        List<GameObject> data = asList(
            newBox(40, 40),
            newBox(0, 0),
            newBox(70, 30),
            newBox(5, 200),
            newBox(5, 100));

        // act
        SweepAndPrune.collide(
            data,
            (o1, o2) -> {
                // assert
                System.out.println(o1.getTransform().getPosition() + " and " + o2.getTransform().getPosition());
            }
        );
    }


    private static GameObject newBox(int x, int y) {
        return GameObject.builder()
            .id(UUID.randomUUID().toString())
            .transform(Transform.builder()
                .position(Vector2d.of(x, y))
                .rotationAngleRadian(0)
                .build())
            .rigidBody(RigidBody.builder()
                .shape(BOX.withOffset(Vector2d.of(x, y)))
                .build())
            .build();
    }
}
