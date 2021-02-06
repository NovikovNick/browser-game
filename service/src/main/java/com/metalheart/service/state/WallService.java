package com.metalheart.service.state;

import com.metalheart.model.common.Vector2d;
import java.util.List;

public interface WallService {
    List<Vector2d> generateMaze();

    List<Vector2d> generateGround();

    List<Vector2d> generateWalls();
}
