package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.game.GameObject;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlayerSnapshotDeltaServiceImpl implements PlayerSnapshotDeltaService {

    public static final int OBJECT_LIMIT = 10;

    @Override
    public PlayerSnapshot calculateDelta(PlayerSnapshot base, List<PlayerSnapshot> sent) {

        PlayerSnapshot res = PlayerSnapshot.builder().build();
        Set<GameObject> walls = new HashSet<>();

        for (PlayerSnapshot snapshot : sent) {

            if (base != null) {
                walls = snapshot.getWalls().stream()
                    .filter(w -> !base.getWalls().contains(w))
                    .collect(Collectors.toSet());
            } else {
                walls.addAll(snapshot.getWalls());
            }

            res = PlayerSnapshot.builder()
                .character(snapshot.getCharacter())
                .enemies(snapshot.getEnemies())
                .projectiles(snapshot.getProjectiles())
                .explosions(snapshot.getExplosions())
                .walls(new ArrayList<>(walls))
                .build();
        }


        return res;
    }
}
