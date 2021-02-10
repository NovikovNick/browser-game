package com.metalheart.model;

import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.GameObject;
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
    private List<GameObject> explosions;
    private List<GameObject> walls;
    private List<String> removedGameObjectIds;

    @Override
    public State clone() {
        return State.builder()
            .playersAckSN(new HashMap<>(playersAckSN))
            .players(new HashMap<>(this.getPlayers()))
            .projectiles(this.getProjectiles())
            .explosions(this.getExplosions())
            .walls(this.getWalls())
            .removedGameObjectIds(this.getRemovedGameObjectIds())
            .build();
    }
}
