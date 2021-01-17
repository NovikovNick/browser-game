package com.metalheart.service;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import java.util.Map;

public interface PlayerSnapshotService {

    Map<String, PlayerSnapshot> splitState(State state);
}
