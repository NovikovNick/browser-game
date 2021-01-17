package com.metalheart.maze;

import com.metalheart.model.common.Vector2d;
import java.util.Map;
import java.util.Stack;
import lombok.Data;

@Data
public class Maze {

    Stack<Vector2d> buildPath;
    Map<Vector2d, MazeCell> data;
}
