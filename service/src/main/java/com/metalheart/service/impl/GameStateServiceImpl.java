package com.metalheart.service.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.service.GameStateService;
import com.metalheart.service.UsernameService;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GameStateServiceImpl implements GameStateService {

    private static final float SPEED = 0.5f;
    private final Map<String, PlayerSnapshot> players;
    private Map<String, Set<PlayerInput>> inputs;


    private UsernameService usernameService;

    public GameStateServiceImpl(UsernameService usernameService) {
        this.usernameService = usernameService;
        this.players = new ConcurrentHashMap<>();
        this.inputs = new ConcurrentHashMap<>();
    }

    @Override
    public void registerPlayer(String playerId) {

        PlayerSnapshot player = PlayerSnapshot.builder()
            .sessionId(playerId)
            .username(usernameService.generateUsername())
            .mousePosX(0)
            .mousePosY(0)
            .characterPosX(0)
            .characterPosY(0)
            .build();

        players.put(playerId, player);
    }

    @Override
    public String updateUsername(String playerId, String username) {

        username = StringUtils.isEmpty(username) ? usernameService.generateUsername() : username;

        PlayerSnapshot player = players.get(playerId);
        player.setUsername(username);
        return username;
    }

    @Override
    public void unregisterPlayer(String playerId) {
        players.remove(playerId);
    }

    @Override
    public void changePlayerState(String playerId, PlayerInput input) {
        inputs.putIfAbsent(playerId, new TreeSet<>(Comparator.comparing(PlayerInput::getTime)));
        inputs.get(playerId).add(input);
    }

    @Override
    public Map<String, PlayerSnapshot> calculateGameState(Integer tickDelay) {

        for (String sessionId : inputs.keySet()) {
            Set<PlayerInput> in = inputs.remove(sessionId);
            Optional<PlayerInput> first = in.stream().findFirst();
            first.ifPresent(req -> {

                if (players.containsKey(sessionId)) {
                    PlayerSnapshot snapshot = players.get(sessionId);
                    snapshot.setMousePosX(req.getMousePosX());
                    snapshot.setMousePosY(req.getMousePosY());

                    if (req.getIsPressedW()) {
                        snapshot.setCharacterPosY((int) (snapshot.getCharacterPosY() - SPEED * tickDelay));
                    }
                    if (req.getIsPressedS()) {
                        snapshot.setCharacterPosY((int) (snapshot.getCharacterPosY() + SPEED * tickDelay));
                    }

                    if (req.getIsPressedA()) {
                        snapshot.setCharacterPosX((int) (snapshot.getCharacterPosX() - SPEED * tickDelay));
                    }
                    if (req.getIsPressedD()) {
                        snapshot.setCharacterPosX((int) (snapshot.getCharacterPosX() + SPEED * tickDelay));
                    }
                }
            });
        }
        return players;
    }
}
