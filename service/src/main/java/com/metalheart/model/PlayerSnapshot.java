package com.metalheart.model;

import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.Player;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "sessionId")
public class PlayerSnapshot {
    private Player character;
    private List<Player> enemies;
    private Set<Bullet> projectiles;
    private List<Vector2d> explosions;
    private List<Polygon2d> walls;
}
