package com.metalheart.service.output;

import com.metalheart.model.PlayerSnapshot;
import java.util.List;

public interface PlayerSnapshotDeltaService {

    PlayerSnapshot calculateDelta(PlayerSnapshot base, List<PlayerSnapshot> sent);
}
