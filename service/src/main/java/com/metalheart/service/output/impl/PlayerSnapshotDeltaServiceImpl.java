package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import com.metalheart.model.PlayerStateProjection;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class PlayerSnapshotDeltaServiceImpl implements PlayerSnapshotDeltaService {

    public static final int GAME_OBJECT_MAX_SIZE = 10;

    @Override
    public PlayerSnapshot getDelta(PlayerStatePresentation base, PlayerStateProjection projection) {

        Set<GameObject> newObjects = new HashSet<>();
        Set<GameObject> removedObjects = new HashSet<>();
        Set<GameObject> movedObjects = new HashSet<>();

        Stream
            .concat(base.getAll().stream(), projection.getAll().stream())
            .distinct()
            .forEach(obj -> {

                long id = obj.getId();
                GameObject fromBase = base.getGameObject(id);
                GameObject fromProjection = projection.getGameObject(id);

                boolean isPresentInBase = fromBase != null;
                boolean isPresentInProjection = fromProjection != null;

                if (isPresentInBase && !isPresentInProjection) {
                    removedObjects.add(fromBase);
                } else if (!isPresentInBase && isPresentInProjection) {
                    newObjects.add(fromProjection);
                } else if (isPresentInBase && isPresentInProjection && !isPositionEqual(fromBase, fromProjection)) {
                    movedObjects.add(fromProjection);
                }
            });



        // get different
        Player player = projection.getPlayer();
        List<Player> enemies = new ArrayList<>();
        Set<Bullet> projectiles = new HashSet<>();
        List<GameObject> explosions = new ArrayList<>();
        List<GameObject> walls = new ArrayList<>();

        Stream
            .concat(movedObjects.stream(), newObjects.stream())
            .limit(GAME_OBJECT_MAX_SIZE)
            .forEach(gameObject -> {

                switch (gameObject.getType()) {
                    case PLAYER:
                        if (!gameObject.equals(player)) {
                            enemies.add((Player) gameObject);
                        }
                        break;
                    case WALL:
                        walls.add(gameObject);
                        break;
                    case EXPLOSION:
                        explosions.add(gameObject);
                        break;
                    case BULLET:
                        projectiles.add((Bullet) gameObject);
                        break;
                }
            });

        PlayerSnapshot res = PlayerSnapshot.builder()
            .character(player)
            .enemies(enemies)
            .projectiles(projectiles)
            .explosions(explosions)
            .walls(walls)
            .removed(removedObjects.stream().map(GameObject::getId).map(String::valueOf).collect(Collectors.toList()))
            .build();

        return res;
    }

    private boolean isPositionEqual(GameObject o1, GameObject o2) {
        return o1.getPos().equals(o2.getPos());
    }
}
