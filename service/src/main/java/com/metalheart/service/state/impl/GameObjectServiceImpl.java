package com.metalheart.service.state.impl;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Material;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.state.GameObjectService;
import com.metalheart.service.state.ShapeService;
import com.metalheart.service.tmp.Body;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class GameObjectServiceImpl implements GameObjectService {

    private final AtomicLong gameObjectSequence;
    private final ShapeService shapeService;

    public GameObjectServiceImpl(ShapeService shapeService) {
        this.shapeService = shapeService;
        gameObjectSequence = new AtomicLong(0);
    }

    @Override
    public GameObject transform(GameObject obj, Vector2d position, float rotationAngleRadian) {

        Polygon2d shape = obj.getRigidBody().getShape();
        Polygon2d transformed = GeometryUtil.rotate(shape.withOffset(position), rotationAngleRadian, position);

        return GameObject.builder()
            .id(obj.getId())
            .transform(Transform.builder()
                .position(position)
                .rotationAngleRadian(rotationAngleRadian)
                .build())
            .rigidBody(RigidBody.builder()
                .shape(shape)
                .transformed(transformed)
                .build())
            .build();
    }

    @Override
    public Body newWall(Vector2d position, float rotationAngleRadian) {
        long id = gameObjectSequence.incrementAndGet();
        return new Body(id, shapeService.wallShape(), Material.STATIC, position);
    }

    @Override
    public Body newPlayer(Vector2d position, float rotationAngleRadian) {
        long id = gameObjectSequence.incrementAndGet();
        return new Body(id, shapeService.playerShape(), Material.BOUNCY_BALL, position);
    }

    private GameObject getGameObject(String id, Vector2d position, float rotationAngleRadian, Polygon2d shape) {

        Polygon2d transformed = GeometryUtil.rotate(shape.withOffset(position), rotationAngleRadian, position);

        return GameObject.builder()
            .id(id)
            .transform(Transform.builder()
                .position(position)
                .rotationAngleRadian(rotationAngleRadian)
                .build())
            .rigidBody(RigidBody.builder()
                .shape(shape)
                .transformed(transformed)
                .build())
            .build();
    }
}
