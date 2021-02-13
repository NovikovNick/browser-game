package com.metalheart.service.output;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import java.util.Map;

public interface OutputService {
    Map<String, PlayerSnapshot> toSnapshots(State state);
}
