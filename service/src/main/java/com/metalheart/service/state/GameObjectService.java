package com.metalheart.service.state;

import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.GameObject;

public interface GameObjectService {

    GameObject newWall(Vector2d position, float rotationAngleRadian);

    Bullet newBullet(Vector2d position, float rotationAngleRadian);

    GameObject newExplosion(Vector2d position, float rotationAngleRadian);

    Player newPlayer(Vector2d position, float rotationAngleRadian);
}
