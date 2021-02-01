package com.metalheart.service.state;

import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import java.util.List;

public interface WallService {
    List<Vector2d> generateMaze();
    List<Vector2d> generateWalls();
}
