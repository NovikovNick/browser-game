package com.metalheart.model;

import com.metalheart.model.game.Player;
import com.metalheart.model.game.Wall;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "sessionId")
public class PlayerSnapshot {
    private Player character;
    private List<Player> enemies;
    private List<Wall> walls;
}
