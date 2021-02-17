package com.metalheart.service.state;

import com.metalheart.model.State;

public interface GameStateService {

    void registerPlayer(String playerId, String id);

    String updateUsername(String playerId, String username);

    void unregisterPlayer(String playerId);

    State step(float tickDelay);
}
