package com.metalheart.converter;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.model.response.GameObjectView;
import com.metalheart.model.response.PlayerView;
import com.metalheart.model.response.SnapshotView;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayerSnapshotToSnapshotViewConverter implements Converter<PlayerSnapshot, SnapshotView> {

    @Override
    public SnapshotView convert(PlayerSnapshot source) {

        PlayerView character = convert(source.getCharacter());

        List<PlayerView> enemies = source.getEnemies().stream()
            .map(this::convert)
            .collect(Collectors.toList());

        List<GameObjectView> walls = source.getWalls().stream()
            .map(this::convertGameObject)
            .collect(Collectors.toList());

        List<GameObjectView> explosions = source.getExplosions().stream()
            .map(this::convertGameObject)
            .collect(Collectors.toList());


        List<GameObjectView> projectiles = source.getProjectiles()
            .stream()
            .map(this::convertGameObject)
            .collect(Collectors.toList());

        return SnapshotView.builder()
            .sn(source.getSequenceNumber())
            .character(character)
            .enemies(enemies)
            .walls(walls)
            .projectiles(projectiles)
            .explosions(explosions)
            .removed(source.getRemoved())
            .build();
    }

    private PlayerView convert(Player source) {
        return PlayerView.builder()
            .username(source.getUsername())
            .obj(this.convertGameObject(source))
            .build();
    }

    private GameObjectView convertGameObject(GameObject source) {
        return GameObjectView.builder()
            .id(source.getId() + "")
            .pos(new float[]{
                source.getPos().getD0(),
                source.getPos().getD1()
            })
            .rot(0)
            .build();
    }
}
