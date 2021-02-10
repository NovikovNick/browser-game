package com.metalheart.service.state.impl;

import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.common.Material;
import com.metalheart.model.game.Player;
import com.metalheart.service.state.GameObjectService;
import com.metalheart.service.state.ShapeService;
import com.metalheart.model.game.GameObject;
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
    public GameObject newWall(Vector2d position, float rotationAngleRadian) {
        long id = gameObjectSequence.incrementAndGet();
        return new GameObject(id, shapeService.wallShape(), Material.STATIC, position);
    }

    @Override
    public Bullet newBullet(Vector2d position, float rotationAngleRadian) {
        long id = gameObjectSequence.incrementAndGet();
        return new Bullet(id, shapeService.bulletShape(), Material.METAL, position);
    }

    @Override
    public GameObject newExplosion(Vector2d position, float rotationAngleRadian) {
        long id = gameObjectSequence.incrementAndGet();
        return new GameObject(id, shapeService.bulletShape(), Material.STATIC, position);
    }

    @Override
    public Player newPlayer(Vector2d position, float rotationAngleRadian) {
        long id = gameObjectSequence.incrementAndGet();
        return new Player(id, shapeService.playerShape(), Material.BOUNCY_BALL, position);
    }
}
