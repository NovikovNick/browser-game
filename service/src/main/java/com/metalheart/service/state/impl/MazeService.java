package com.metalheart.service.state.impl;

import com.metalheart.maze.Maze;
import com.metalheart.maze.MazeDoorDirection;
import com.metalheart.maze.RecursiveBacktrackerMazeBuilder;
import com.metalheart.model.common.Vector2d;
import com.metalheart.service.state.WallService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MazeService implements WallService {

    public MazeService() {
    }

    @Override
    public List<Vector2d> generateGround() {
        List<Vector2d> list = new ArrayList<>();

        int size = 100;
        Vector2d offset = Vector2d.of(-2000, 300);

        int width = 40;
        int height = 3;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                list.add(Vector2d.of(x, y).scale(size).plus(offset));
            }
        }
        return list;
    }

    @Override
    public List<Vector2d> generateWalls() {
        List<Vector2d> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            list.add(Vector2d.of(i * 100 - 300, 200));
            list.add(Vector2d.of(i * 100 - 300, -300));
            list.add(Vector2d.of(-400, i * 100 - 300));
            list.add(Vector2d.of(300, i * 100 - 300));
        }
        return list;
    }

    @Override
    public List<Vector2d> generateMaze() {

        RecursiveBacktrackerMazeBuilder mazeBuilder = new RecursiveBacktrackerMazeBuilder()
            .setWidth(5)
            .setHeight(5)
            .setEnter(new Vector2d(0, 0))
            .setEnterDirection(MazeDoorDirection.LEFT)
            .setExit(new Vector2d(5, 4))
            .setExitDirection(MazeDoorDirection.RIGHT);

        Maze maze = new Maze();
        while (!mazeBuilder.isFinished((maze = mazeBuilder.buildNextStep(maze)))){}

        List<Vector2d> walls = new ArrayList<>();

        maze.getData().forEach((point, cell) -> {

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {

                    Vector2d go = Vector2d.of(
                        point.getD0() * 400 +  x * 100,
                        point.getD1() * -400 +  y * 100);

                    if ((x == 0 || x == 3) && (y == 0 || y == 3)) {
                        walls.add(go);
                    }
                    if (!cell.getDirections().contains(MazeDoorDirection.LEFT) && (x == 0 && (y == 1 || y == 2))) {
                        walls.add(go);
                    }
                    if (!cell.getDirections().contains(MazeDoorDirection.RIGHT) && (x == 3 && (y == 1 || y == 2))) {
                        walls.add(go);
                    }
                    if (!cell.getDirections().contains(MazeDoorDirection.TOP) && (y == 0 && (x == 1 || x == 2))) {
                        walls.add(go);
                    }
                    if (!cell.getDirections().contains(MazeDoorDirection.BOTTOM) && (y == 3 && (x == 1 || x == 2))) {
                        walls.add(go);
                    }
                }
            }
        });

        return walls;
    }
}
