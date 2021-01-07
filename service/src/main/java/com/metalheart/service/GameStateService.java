package com.metalheart.service;

import com.metalheart.model.Player;
import com.metalheart.model.Point2d;
import java.util.Map;

public interface GameStateService {

    void registerPlayer(String playerId);

    String updateUsername(String playerId, String username);

    void unregisterPlayer(String playerId);

    void changePlayerState(String playerId, Point2d point);

    Map<String, Player> getGameState();
}
