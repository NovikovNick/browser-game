package com.metalheart.service.state;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.service.tmp.Body;

public interface GameObjectService {

    GameObject transform(GameObject obj, Vector2d position, float rotationAngleRadian);

    Body newWall(Vector2d position, float rotationAngleRadian);

    Body newPlayer(Vector2d position, float rotationAngleRadian);
}
