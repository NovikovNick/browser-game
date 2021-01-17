package com.metalheart.model;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.Player;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class State implements Cloneable {
    private Map<String, Player> players;
    private Set<Bullet> projectiles;
    private List<Vector2d> explosions;
    private List<Polygon2d> walls;

    @Override
    public State clone() {
        return State.builder()
            .players(this.getPlayers())
            .projectiles(this.getProjectiles())
            .explosions(this.getExplosions())
            .walls(this.getWalls())
            .build();
    }
}
