package com.metalheart.service.output;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import com.metalheart.model.PlayerStateProjection;
import com.metalheart.model.game.Player;
import java.util.List;

public interface PlayerSnapshotDeltaService {
    PlayerSnapshot getDelta(PlayerStatePresentation base, PlayerStateProjection projection);
}
