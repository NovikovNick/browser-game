package com.metalheart.maze;

import com.metalheart.model.common.Vector2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.metalheart.maze.MazeDoorDirection.BOTTOM;
import static com.metalheart.maze.MazeDoorDirection.LEFT;
import static com.metalheart.maze.MazeDoorDirection.RIGHT;
import static com.metalheart.maze.MazeDoorDirection.TOP;

@Setter
@Accessors(chain = true)
public class RecursiveBacktrackerMazeBuilder {

    private int width;
    private int height;

    private Vector2d enter;
    private MazeDoorDirection enterDirection;

    private Vector2d exit;
    private MazeDoorDirection exitDirection;

    public Maze buildNextStep(Maze maze) {

        if (maze.getData() == null) {
            maze.setData(new HashMap<>());
            maze.setBuildPath(new Stack<>());
            MazeCell cell = new MazeCell();
            cell.getDirections().add(enterDirection);
            maze.getData().put(enter, cell);
            maze.getBuildPath().push(enter);
            return maze;
        }


        List<MazeDoorDirection> availablePath = getAvailablePath(maze);

        if (!availablePath.isEmpty()) {

            Vector2d currentCell = maze.getBuildPath().peek();

            float x = currentCell.getD0();
            float y = currentCell.getD1();

            Vector2d toTop = Vector2d.of(x, y + 1);
            Vector2d toBottom = Vector2d.of(x, y - 1);
            Vector2d toLeft = Vector2d.of(x - 1, y);
            Vector2d toRight = Vector2d.of(x + 1, y);

            MazeCell cell = new MazeCell();
            MazeDoorDirection randomDirection = availablePath.get(new Random().nextInt(availablePath.size()));


            Vector2d nextCell = null;
            switch (randomDirection) {
                case TOP:
                    nextCell = toTop;
                    break;
                case BOTTOM:
                    nextCell = toBottom;
                    break;
                case LEFT:
                    nextCell = toLeft;
                    break;
                case RIGHT:
                    nextCell = toRight;
                    break;
                default:
                    throw new IllegalStateException();
            }
            maze.getData().get(currentCell).getDirections().add(randomDirection);
            cell.getDirections().add(randomDirection.getOpposite());

            if (nextCell.equals(exit)) {
                cell.getDirections().add(exitDirection);
            }

            maze.getData().put(nextCell, cell);
            maze.getBuildPath().push(nextCell);

            return maze;

        } else if (!maze.getBuildPath().isEmpty()) {
            maze.getBuildPath().pop();
        }
        return maze;
    }

    private List<MazeDoorDirection> getAvailablePath(Maze maze) {

        if(maze.getBuildPath().isEmpty()){
            return Collections.emptyList();
        }
        Vector2d currentCell = maze.getBuildPath().peek();

        float x = currentCell.getD0();
        float y = currentCell.getD1();

        Vector2d toTop = Vector2d.of(x, y + 1);
        Vector2d toBottom = Vector2d.of(x, y - 1);
        Vector2d toLeft = Vector2d.of(x - 1, y);
        Vector2d toRight = Vector2d.of(x + 1, y);

        List<MazeDoorDirection> availablePath = new ArrayList<>();
        if (y < height && !maze.getData().containsKey(toTop)) {
            availablePath.add(TOP);
        }
        if (y > 0 && !maze.getData().containsKey(toBottom)) {
            availablePath.add(BOTTOM);
        }
        if (x > 0 && !maze.getData().containsKey(toLeft)) {
            availablePath.add(LEFT);
        }
        if (x < width && !maze.getData().containsKey(toRight)) {
            availablePath.add(RIGHT);
        }
        return availablePath;
    }

    public boolean isFinished(Maze maze) {

        return maze.getData() != null && maze.getData().values().size() == ((width+1) * (height+1));
    }
}
