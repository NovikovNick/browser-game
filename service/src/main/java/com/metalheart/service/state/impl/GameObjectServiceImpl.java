package com.metalheart.service.state.impl;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.service.state.GameObjectService;
import com.metalheart.service.GeometryUtil;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GameObjectServiceImpl implements GameObjectService {

    @Override
    public GameObject newGameObject(Vector2d position, float rotationAngleRadian, Polygon2d shape) {
        return getGameObject(UUID.randomUUID().toString(), position, rotationAngleRadian, shape);
    }

    @Override
    public GameObject withOrigin(Vector2d origin, GameObject obj) {

        return GameObject.builder()
            .id(obj.getId())
            .transform(Transform.builder()
                .position(obj.getTransform().getPosition().plus(origin))
                .rotationAngleRadian(obj.getTransform().getRotationAngleRadian())
                .build())
            .rigidBody(RigidBody.builder()
                .shape(obj.getRigidBody().getShape())
                .transformed(obj.getRigidBody().getTransformed().withOffset(origin))
                .build())
            .build();
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
