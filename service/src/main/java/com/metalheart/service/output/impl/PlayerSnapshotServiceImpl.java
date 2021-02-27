package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerStateProjection;
import com.metalheart.model.State;
import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.service.output.PlayerSnapshotService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

import static com.metalheart.model.game.GameObjectType.PLAYER;

@Service
public class PlayerSnapshotServiceImpl implements PlayerSnapshotService {

    public static final int FIELD_OF_VIEW = 400;

    private Lock lock;

    public PlayerSnapshotServiceImpl() {
        this.lock = new ReentrantLock();
    }

    @Override
    public Map<String, PlayerStateProjection> splitState(State state) {


        Map<String, PlayerStateProjection> res = new HashMap<>();
        lock.lock();
        try {

            Map<String, Player> players = state.getPlayers();
            players.forEach((id, player) -> {

                PlayerStateProjection projection = new PlayerStateProjection();

                for (GameObject gameObject : state.getAll()) {

                    if (!isVisible(player.getPos(), gameObject.getShapePositioned())) {
                        continue;
                    }

                    if (PLAYER.equals(gameObject.getType())) {
                        if (gameObject.equals(player)) {
                            projection.setPlayer(player);
                        } else {
                            projection.addEnemy((Player) gameObject.clone());
                        }
                    } else {
                        projection.addGameObject(gameObject.clone());
                    }
                }

                res.put(id, projection);
            });
        } finally {
            lock.unlock();
        }
        return res;
    }

    private boolean isVisible(Vector2d playerPos, Vector2d objPos) {
        return Math.abs(playerPos.getD0() - objPos.getD0()) < FIELD_OF_VIEW
            && Math.abs(playerPos.getD1() - objPos.getD1()) < FIELD_OF_VIEW;
    }

    private boolean isVisible(Vector2d playerPos, Polygon2d polygon) {
        AABB2d aabb = AABB2d.of(polygon.getPoints());
        return isVisible(playerPos, aabb.getMax())
            || isVisible(playerPos, aabb.getMin())
            || isVisible(playerPos, aabb.getCenter());
    }
}
