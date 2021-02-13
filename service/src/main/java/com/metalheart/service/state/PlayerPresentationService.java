package com.metalheart.service.state;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import java.util.Set;

public interface PlayerPresentationService {

    PlayerStatePresentation getPlayerStatePresentation(String playerId);

    void saveSnapshot(String playerId, PlayerSnapshot snapshot);

    void confirmSnapshots(String playerId, Set<Long> ackSN);
}
