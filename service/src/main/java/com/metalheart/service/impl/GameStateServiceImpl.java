package com.metalheart.service.impl;

import com.metalheart.model.Player;
import com.metalheart.model.Point2d;
import com.metalheart.service.GameStateService;
import com.metalheart.service.UsernameService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GameStateServiceImpl implements GameStateService {

    private final Map<String, Player> players;

    private UsernameService usernameService;

    public GameStateServiceImpl(UsernameService usernameService) {
        this.usernameService = usernameService;
        this.players = new ConcurrentHashMap<>();
    }

    @Override
    public void registerPlayer(String playerId) {

        Player player = Player.builder()
            .id(playerId)
            .username(usernameService.generateUsername())
            .x(0)
            .y(0)
            .build();

        players.put(playerId, player);
    }

    @Override
    public String updateUsername(String playerId, String username) {

        username = StringUtils.isEmpty(username) ? usernameService.generateUsername() : username;

        Player player = players.get(playerId);
        player.setUsername(username);
        return username;
    }

    @Override
    public void unregisterPlayer(String playerId) {
        players.remove(playerId);
    }

    @Override
    public void changePlayerState(String playerId, Point2d point) {
        Player player = players.get(playerId);
        player.setX(point.getD0());
        player.setY(point.getD1());
    }

    @Override
    public Map<String, Player> getGameState() {
        return players;
    }
}
