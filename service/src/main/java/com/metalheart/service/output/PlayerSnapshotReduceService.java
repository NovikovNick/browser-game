package com.metalheart.service.output;

import com.metalheart.model.PlayerSnapshot;

public interface PlayerSnapshotReduceService {
    PlayerSnapshot reduce(PlayerSnapshot snapshot);
}
