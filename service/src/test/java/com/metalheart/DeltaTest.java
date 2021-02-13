package com.metalheart;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Player;
import com.metalheart.service.output.impl.OutputServiceImpl;
import com.metalheart.service.output.impl.PlayerSnapshotDeltaServiceImpl;
import com.metalheart.service.output.impl.PlayerSnapshotServiceImpl;
import com.metalheart.service.state.GameObjectService;
import com.metalheart.service.state.impl.GameObjectServiceImpl;
import com.metalheart.service.state.impl.ShapeServiceImpl;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeltaTest {

    private OutputServiceImpl agentPoolCreator;
    private GameObjectService gameObjectService;

    public DeltaTest() { // todo init by spring context
        this.agentPoolCreator = new OutputServiceImpl(
            new PlayerSnapshotServiceImpl(),
            new PlayerSnapshotDeltaServiceImpl()
        );
        this.gameObjectService = new GameObjectServiceImpl(new ShapeServiceImpl());
    }

    @Test
    public void test() {
        // arrange
        Map<String, Player> players = Map.of(
            "0", gameObjectService.newPlayer(Vector2d.ZERO_VECTOR, 0)
        );
        State state = State.builder().players(players).build();
        // act
        Map<String, PlayerSnapshot> delta = agentPoolCreator.toSnapshots(state);
        // assert
        System.out.println(delta);
    }


}
