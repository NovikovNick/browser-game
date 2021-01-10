package com.metalheart.service;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerInput;
import java.util.Map;
import java.util.UUID;

public interface GameStateService {

    void registerPlayer(String playerId, String id);

    String updateUsername(String playerId, String username);

    void unregisterPlayer(String playerId);

    void changePlayerState(String playerId, PlayerInput input);

    Map<String, PlayerSnapshot> calculateGameState(Integer tickDelay);
}
