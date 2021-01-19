package com.metalheart.service.state;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;

public interface GameObjectService {

    GameObject newGameObject(Vector2d position, float rotationAngleRadian, Polygon2d shape);

    GameObject withOrigin(Vector2d origin, GameObject obj);
}
