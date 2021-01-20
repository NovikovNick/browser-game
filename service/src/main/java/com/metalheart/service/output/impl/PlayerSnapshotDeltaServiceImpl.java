package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.game.GameObject;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlayerSnapshotDeltaServiceImpl implements PlayerSnapshotDeltaService {

    public static final int OBJECT_LIMIT = 10;

    @Override
    public PlayerSnapshot calculateDelta(PlayerSnapshot previous, PlayerSnapshot next) {

        List<GameObject> walls = new ArrayList<>(previous.getWalls())
            .stream()
            .limit(OBJECT_LIMIT)
            .collect(Collectors.toList());

        if (next != null) {
            previous.getWalls().stream()
                .filter(w -> !next.getWalls().contains(w))
                .forEach(w -> {
                    if (walls.size() < OBJECT_LIMIT) walls.add(w);
                });
        }

        return PlayerSnapshot.builder()
            .character(previous.getCharacter())
            .enemies(previous.getEnemies())
            .projectiles(previous.getProjectiles())
            .explosions(previous.getExplosions())
            .walls(walls)
            .build();
    }
}
