package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.service.output.PlayerSnapshotReduceService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SnapshotReduceServiceImpl implements PlayerSnapshotReduceService {

    public static final int GAME_OBJECT_MAX_SIZE = 10;

    @Override
    public PlayerSnapshot reduce(PlayerSnapshot snapshot) {

        PlayerSnapshot res = PlayerSnapshot.builder()
            .character(snapshot.getCharacter())
            .removed(snapshot.getRemoved())
            .build();

        int limit = GAME_OBJECT_MAX_SIZE;

        // enemies
        {
            List<Player> objects = snapshot.getEnemies()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .collect(Collectors.toList());

            res.setEnemies(new ArrayList<>());
            for (Player object : objects) {
                if (limit > 0) {
                    res.getEnemies().add(object);
                    limit--;
                } else {
                    res.getRemoved().add(object.getId() + "");
                }
            }
        }


        // projectiles
        if (snapshot.getProjectiles().size() >= limit) {
            res.setProjectiles(snapshot.getProjectiles()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toSet()));
            return res;
        }
        res.setProjectiles(snapshot.getProjectiles());
        limit -= snapshot.getProjectiles().size();

        // explosions
        if (snapshot.getExplosions().size() >= limit) {
            res.setExplosions(snapshot.getExplosions()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toList()));
            return res;
        }
        res.setExplosions(snapshot.getExplosions());
        limit -= snapshot.getExplosions().size();

        // walls
        if (snapshot.getWalls().size() >= limit) {
            List<GameObject> objects = snapshot.getWalls()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toList());
            res.setWalls(objects);
            return res;
        }
        res.setWalls(snapshot.getWalls());


        return res;
    }

    /*@Override
    public PlayerSnapshot reduce(PlayerSnapshot snapshot) {

        PlayerSnapshot res = PlayerSnapshot.builder()
            .character(snapshot.getCharacter())
            .removed(snapshot.getRemoved())
            .build();

        int limit = GAME_OBJECT_MAX_SIZE;

        // enemies
        if (snapshot.getEnemies().size() >= limit) {
            res.setEnemies(snapshot.getEnemies()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toList()));
            return res;
        }
        res.setEnemies(snapshot.getEnemies());
        limit -= snapshot.getEnemies().size();

        // projectiles
        if (snapshot.getProjectiles().size() >= limit) {
            res.setProjectiles(snapshot.getProjectiles()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toSet()));
            return res;
        }
        res.setProjectiles(snapshot.getProjectiles());
        limit -= snapshot.getProjectiles().size();

        // explosions
        if (snapshot.getExplosions().size() >= limit) {
            res.setExplosions(snapshot.getExplosions()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toList()));
            return res;
        }
        res.setExplosions(snapshot.getExplosions());
        limit -= snapshot.getExplosions().size();

        // walls
        if (snapshot.getWalls().size() >= limit) {
            List<GameObject> objects = snapshot.getWalls()
                .stream()
                .sorted(byDistanceTo(snapshot.getCharacter().getPos()))
                .limit(limit)
                .collect(Collectors.toList());
            res.setWalls(objects);
            return res;
        }
        res.setWalls(snapshot.getWalls());


        return res;
    }*/

    private Comparator<? super GameObject> byDistanceTo(Vector2d position) {

        return Comparator.<GameObject>comparingInt(obj -> {
            int dist0 = (int) Math.abs(position.getD0() - obj.getPos().getD0());
            int dist1 = (int) Math.abs(position.getD1() - obj.getPos().getD1());
            return Math.max(dist0, dist1);
        });
    }

    private boolean isPositionEqual(GameObject o1, GameObject o2) {
        return o1.getPos().equals(o2.getPos());
    }
}
