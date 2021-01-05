package com.metalheart.service.impl;

import com.metalheart.model.Point2d;
import com.metalheart.service.GameStateService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GameStateServiceImpl implements GameStateService {

    private final Map<String, Point2d> players;

    public GameStateServiceImpl() {
        this.players = new ConcurrentHashMap<>();
    }

    @Override
    public void registerPlayer(String playerId) {
        players.put(playerId, new Point2d(0, 0));
    }

    @Override
    public void unregisterPlayer(String playerId) {
        players.remove(playerId);
    }

    @Override
    public void changePlayerState(String playerId, Point2d point) {
        players.put(playerId, point);
    }

    @Override
    public Map<String, Point2d> getGameState() {
        return players;
    }
}
