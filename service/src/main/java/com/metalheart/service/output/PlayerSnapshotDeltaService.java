package com.metalheart.service.output;

import com.metalheart.model.PlayerSnapshot;

public interface PlayerSnapshotDeltaService {

    PlayerSnapshot calculateDelta(PlayerSnapshot s1, PlayerSnapshot s2);
}
