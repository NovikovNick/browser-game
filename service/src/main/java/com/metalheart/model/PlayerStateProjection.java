package com.metalheart.model;

import com.metalheart.model.game.Player;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

public class PlayerStateProjection extends State{

    @Getter
    private Player player;
    @Getter
    private Set<Player> enemies;

    public PlayerStateProjection() {
        this.enemies = new HashSet<>();
    }

    public void setPlayer(Player player) {
        this.player = player;
        addPlayer(player.getSessionId(), player);
    }

    public void addEnemy(Player player) {
        getEnemies().add(player);
        addPlayer(player.getSessionId(), player);
    }
}
