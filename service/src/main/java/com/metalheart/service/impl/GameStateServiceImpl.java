package com.metalheart.service.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.model.game.Wall;
import com.metalheart.service.GameStateService;
import com.metalheart.service.UsernameService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private final Map<String, Player> players;
    private final Map<String, Set<PlayerInput>> inputs;
    private final List<Wall> walls;


    private UsernameService usernameService;

    public GameStateServiceImpl(UsernameService usernameService) {
        this.usernameService = usernameService;
        this.players = new ConcurrentHashMap<>();
        this.walls = new ArrayList<>();
        this.inputs = new ConcurrentHashMap<>();
    }

    @Override
    public void registerPlayer(String sessionId) {

        Player player = Player.builder()
            .mousePos(Vector2d.ZERO_VECTOR)
            .gameObject(GameObject.builder()
                .transform(Transform.builder()
                    .position(Vector2d.ZERO_VECTOR)
                    .rotation(Vector2d.ZERO_VECTOR)
                    .build())
                .rigidBody(RigidBody.builder()
                    .shape(Polygon2d.rectangle())
                    .build())
                .build())
            .sessionId(sessionId)
            .username(usernameService.generateUsername())
            .build();

        players.put(sessionId, player);
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
                    Player player = players.get(sessionId);
                    player.setMousePos(new Vector2d(req.getMousePosX(), req.getMousePosY()));

                    float magnitude = SPEED * tickDelay;

                    Vector2d direction = Vector2d.ZERO_VECTOR;
                    if (req.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
                    if (req.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
                    if (req.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
                    if (req.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);


                    player.setGameObject(GameObject.builder()
                        .transform(Transform.builder()
                            .position(player.getGameObject().getTransform().getPosition().plus(direction.scale(magnitude)))
                            .rotation(Vector2d.ZERO_VECTOR)
                            .build())
                        .rigidBody(RigidBody.builder()
                            .shape(Polygon2d.rectangle())
                            .build())
                        .build());
                }
            });
        }

        Map<String, PlayerSnapshot> snapshots = new HashMap<>();
        players.forEach((id, player) -> {

            List<Player> enemies = new ArrayList<>(players.values());
            enemies.remove(player);

            PlayerSnapshot snapshot = PlayerSnapshot.builder()
                .character(player)
                .enemies(enemies)
                .walls(Collections.emptyList())
                .build();
            snapshots.put(id, snapshot);
        });

        return snapshots;
    }
}
