package com.metalheart;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import com.metalheart.model.PlayerStateProjection;
import com.metalheart.model.common.Material;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.Wall;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import com.metalheart.service.output.impl.PlayerSnapshotDeltaServiceImpl;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeltaTest {

    private static final AtomicLong OBJECT_SEQ = new AtomicLong(0);

    public static final Polygon2d BOUNDING_BOX = new Polygon2d(
        new Vector2d(-50, -50),
        new Vector2d(50, -50),
        new Vector2d(50, 50),
        new Vector2d(-50, 50)
    );

    @Test
    public void test() {
        // arrange

        PlayerSnapshotDeltaService deltaService = new PlayerSnapshotDeltaServiceImpl();

        PlayerStateProjection projection = new PlayerStateProjection();
        projection.setPlayer(newPlayer());
        projection.addGameObject(newWall());

        PlayerStatePresentation base = new PlayerStatePresentation();

        // act
        PlayerSnapshot snapshot = deltaService.getDelta(base, projection);

        // assert
        Assert.assertEquals(1, snapshot.getWalls().size());
        Assert.assertNotNull(snapshot.getCharacter());
    }

    @Test
    public void test2() {
        // arrange
        PlayerSnapshotDeltaService deltaService = new PlayerSnapshotDeltaServiceImpl();

        Player player = newPlayer();
        Player enemy = newPlayer();

        PlayerStateProjection projection = new PlayerStateProjection();
        projection.setPlayer(player);

        PlayerStatePresentation base = new PlayerStatePresentation();
        base.setPlayer(player);
        base.addEnemy(enemy);

        // act
        PlayerSnapshot snapshot = deltaService.getDelta(base, projection);

        // assert
        Assert.assertTrue(snapshot.getRemoved().contains(String.valueOf(enemy.getId())));
    }

    private Player newPlayer() {
        long id = OBJECT_SEQ.incrementAndGet();
        Player player = new Player(id, BOUNDING_BOX, Material.WOOD, Vector2d.ZERO_VECTOR);
        player.setSessionId("sessionId_" + id);
        return player;
    }

    private Player newPlayer(Vector2d pos) {
        long id = OBJECT_SEQ.incrementAndGet();
        Player player = new Player(id, BOUNDING_BOX, Material.WOOD, pos);
        player.setSessionId("sessionId_" + id);
        return player;
    }

    private Wall newWall() {
        long id = OBJECT_SEQ.incrementAndGet();
        return new Wall(id, BOUNDING_BOX, Material.WOOD, Vector2d.ZERO_VECTOR);
    }
}
