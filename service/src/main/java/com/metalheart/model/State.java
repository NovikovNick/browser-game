package com.metalheart.model;

import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class State implements Cloneable {

    private Map<String, Long> playersAckSN;
    private Map<String, Player> players;
    private Set<Bullet> projectiles;
    private List<Vector2d> explosions;
    private List<GameObject> walls;

    @Override
    public State clone() {
        return State.builder()
            .playersAckSN(new HashMap<>(playersAckSN))
            .players(new HashMap<>(this.getPlayers()))
            .projectiles(this.getProjectiles())
            .explosions(this.getExplosions())
            .walls(this.getWalls())
            .build();
    }
}
