package com.metalheart.service.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.service.GameObjectService;
import com.metalheart.service.PlayerSnapshotService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlayerSnapshotServiceImpl implements PlayerSnapshotService {

    private final GameObjectService gameObjectService;
    private Lock lock;

    public PlayerSnapshotServiceImpl(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
        this.lock = new ReentrantLock();
    }

    @Override
    public Map<String, PlayerSnapshot> splitState(State state) {

        Map<String, PlayerSnapshot> snapshots = new HashMap<>();
        lock.lock();
        try {

            Map<String, Player> players = state.getPlayers();
            Set<Bullet> projectiles = state.getProjectiles();
            List<Vector2d> explosions = state.getExplosions();
            List<GameObject> walls = state.getWalls();

            players.forEach((id, player) -> {

                Player cloned = player.clone();
                Vector2d offset = cloned.getGameObject().getTransform().getPosition().reversed();

                cloned.setGameObject(GameObject.builder()
                    .transform(Transform.builder()
                        .position(Vector2d.ZERO_VECTOR)
                        .rotationAngleRadian(cloned.getGameObject().getTransform().getRotationAngleRadian())
                        .build())
                    .rigidBody(RigidBody.builder()
                        .shape(cloned.getGameObject().getRigidBody().getShape())
                        .transformed(cloned.getGameObject().getRigidBody().getTransformed().withOffset(offset))
                        .build())
                    .build());

                List<Player> enemies = players.values().stream()
                    .filter(enemy -> !player.equals(enemy))
                    .map(enemy -> Player.builder()
                        .id(enemy.getId())
                        .sessionId(enemy.getSessionId())
                        .username(enemy.getUsername())
                        .gameObject(gameObjectService.withOrigin(offset, enemy.getGameObject()))
                        .build())
                    .collect(Collectors.toList());

                PlayerSnapshot snapshot = PlayerSnapshot.builder()
                    .character(cloned)
                    .enemies(enemies)
                    .projectiles(projectiles.stream().map(p -> Bullet.builder()
                        .id(p.getId())
                        .playerId(p.getPlayerId())
                        .gameObject(gameObjectService.withOrigin(offset, p.getGameObject()))
                        .build()).collect(Collectors.toSet()))
                    .explosions(explosions.stream()
                        .map(offset::plus)
                        .collect(Collectors.toList()))
                    .walls(walls.stream().map(w -> gameObjectService.withOrigin(offset, w)).collect(Collectors.toList()))
                    .build();
                snapshots.put(id, snapshot);
            });
        } finally {
            lock.unlock();
        }
        return snapshots;
    }
}
