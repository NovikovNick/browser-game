package com.metalheart.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnapshotView {
    private long sn;
    private PlayerView character;
    private List<PlayerView> enemies;
    private List<GameObjectView> walls;
    private List<GameObjectView> projectiles;
    private List<String> removed;
}

