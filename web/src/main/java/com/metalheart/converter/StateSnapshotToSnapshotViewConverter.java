package com.metalheart.converter;

import com.metalheart.model.StateSnapshot;
import com.metalheart.model.game.Bullet;
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
public class StateSnapshotToSnapshotViewConverter implements Converter<StateSnapshot, SnapshotView> {

    @Override
    public SnapshotView convert(StateSnapshot source) {

        PlayerView character = convert(source.getSnapshot().getCharacter());

        List<PlayerView> enemies = source.getSnapshot().getEnemies().stream()
                .map(this::convert)
                .collect(Collectors.toList());

        List<GameObjectView> walls = source.getSnapshot().getWalls().stream()
            .map(this::convert)
            .collect(Collectors.toList());


        List<GameObjectView> projectiles = source.getSnapshot().getProjectiles()
            .stream()
            .map(Bullet::getGameObject)
            .map(this::convert)
            .collect(Collectors.toList());

        return SnapshotView.builder()
            .sn(source.getSequenceNumber())
            .character(character)
            .enemies(enemies)
            .walls(walls)
            .projectiles(projectiles)
            .removed(source.getSnapshot().getRemoved())
            .build();
    }

    private PlayerView convert(Player source) {
        return PlayerView.builder()
            .username(source.getUsername())
            .obj(this.convert(source.getGameObject()))
            .build();
    }

    private GameObjectView convert(GameObject source) {
        return GameObjectView.builder()
            .id(source.getId())
            .pos(new float[]{
                source.getTransform().getPosition().getD0(),
                source.getTransform().getPosition().getD1()
            })
            .rot(source.getTransform().getRotationAngleRadian())
            .build();
    }
}
