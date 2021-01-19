package com.metalheart.service.output;

import com.metalheart.model.State;
import com.metalheart.model.StateSnapshot;
import java.util.Map;

public interface OutputService {
    Map<String, StateSnapshot> toSnapshots(State state);
}
