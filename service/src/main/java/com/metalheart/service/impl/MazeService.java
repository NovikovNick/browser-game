package com.metalheart.service.impl;

import com.metalheart.maze.Maze;
import com.metalheart.maze.MazeDoorDirection;
import com.metalheart.maze.RecursiveBacktrackerMazeBuilder;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.service.GameObjectService;
import com.metalheart.service.ShapeService;
import com.metalheart.service.WallService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MazeService implements WallService {


    private final GameObjectService gameObjectService;
    private final ShapeService shapeService;


    public MazeService(GameObjectService gameObjectService, ShapeService shapeService) {
        this.gameObjectService = gameObjectService;
        this.shapeService = shapeService;
    }

    @Override
    public List<GameObject> generateWalls() {

        RecursiveBacktrackerMazeBuilder mazeBuilder = new RecursiveBacktrackerMazeBuilder()
            .setWidth(5)
            .setHeight(5)
            .setEnter(new Vector2d(0, 0))
            .setEnterDirection(MazeDoorDirection.LEFT)
            .setExit(new Vector2d(5, 4))
            .setExitDirection(MazeDoorDirection.RIGHT);

        Maze maze = new Maze();
        while (!mazeBuilder.isFinished((maze = mazeBuilder.buildNextStep(maze)))){}


        List<GameObject> walls = new ArrayList<>();

        maze.getData().forEach((point, cell) -> {

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {

                    GameObject go = gameObjectService.newGameObject(Vector2d.of(
                        point.getD0() * 400 +  x * 100,
                        point.getD1() * -400 +  y * 100),
                        0,
                        shapeService.wallBoundingBox());

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
