package com.metalheart.service.output;

import com.metalheart.model.PlayerStateProjection;
import com.metalheart.model.State;
import java.util.Map;

public interface PlayerSnapshotService {

    Map<String, PlayerStateProjection> splitState(State state);
}
