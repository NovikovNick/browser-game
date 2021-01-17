package com.metalheart.maze;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MazeCell {

    private boolean isEnter;
    private boolean isExit;
    private List<MazeDoorDirection> directions = new ArrayList<>();
}
