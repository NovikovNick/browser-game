package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.service.output.PlayerSnapshotService;
import com.metalheart.service.tmp.Body;
import java.util.ArrayList;
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

    public static final int FIELD_OF_VIEW = 600;

    private Lock lock;

    public PlayerSnapshotServiceImpl() {
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
            List<Body> walls = state.getWalls();

            players.forEach((id, player) -> {

                Player cloned = player.clone();

                List<Player> enemies = players.values().stream()
                    .filter(enemy -> !player.equals(enemy))
                    .map(enemy -> Player.builder()
                        .id(enemy.getId())
                        .sessionId(enemy.getSessionId())
                        .username(enemy.getUsername())
                        .gameObject(enemy.getGameObject())
                        .build())
                    .collect(Collectors.toList());


                List<String> removedWalls = new ArrayList<>();
                List<Body> playerWalls = new ArrayList<>();

                for (Body wall : walls) {
                    Vector2d playerPos = player.getGameObject().getPos();
                    Vector2d wallPos = wall.getPos();

                    if (Math.abs(playerPos.getD0() - wallPos.getD0()) < FIELD_OF_VIEW
                        && Math.abs(playerPos.getD1() - wallPos.getD1()) < FIELD_OF_VIEW) {
                        playerWalls.add(wall);
                    } else {
                        removedWalls.add(wall.getId() + "");
                    }
                }

                PlayerSnapshot snapshot = PlayerSnapshot.builder()
                    .character(cloned)
                    .enemies(enemies)
                    .projectiles(projectiles.stream().map(p -> Bullet.builder()
                        .id(p.getId())
                        .playerId(p.getPlayerId())
                        .gameObject(p.getGameObject())
                        .build()).collect(Collectors.toSet()))
                    .explosions(explosions)
                    .walls(playerWalls)
                    .removed(removedWalls)
                    .build();
                snapshots.put(id, snapshot);
            });
        } finally {
            lock.unlock();
        }
        return snapshots;
    }
}
