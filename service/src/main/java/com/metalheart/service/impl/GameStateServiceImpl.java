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

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.stream.Collectors.toList;

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

        Vector2d start = Vector2d.of(800, 200);

        Player player = Player.builder()
            .mousePos(start)
            .gameObject(GameObject.builder()
                .transform(Transform.builder()
                    .position(start)
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
        // todo add lock
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
                    Vector2d mousePos = Vector2d.of(req.getMousePosX(), req.getMousePosY());
                    player.setMousePos(mousePos);

                    Vector2d direction = Vector2d.ZERO_VECTOR;
                    if (req.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
                    if (req.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);
                    if (req.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
                    if (req.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
                    direction = direction.normalize();

                    Transform transform = player.getGameObject().getTransform();
                    float angleRadian = getAngleRadian(mousePos, transform.getPosition());
                    direction = rotate(direction,  angleRadian, Vector2d.ZERO_VECTOR);

                    float magnitude = SPEED * tickDelay;
                    Vector2d force = direction.scale(magnitude);

                    Vector2d center = transform.getPosition().plus(force);

                    RigidBody rigidBody = player.getGameObject().getRigidBody();
                    Polygon2d shape = rigidBody.getShape();
                    Polygon2d transformed = new Polygon2d(shape.getPoints().stream()
                        .map(p -> rotate(p.plus(center), angleRadian, center))
                        .collect(toList()));
                    player.setGameObject(GameObject.builder()
                        .transform(Transform.builder()
                            .position(center)
                            .rotation(mousePos)
                            .build())
                        .rigidBody(RigidBody.builder()
                            .shape(shape)
                            .transformed(transformed)
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

    private static float getAngleRadian(Vector2d p1, Vector2d p2) {
        float deltaX = p2.getD0() - p1.getD0();
        float deltaY = p2.getD1() - p1.getD1();
        return (float) Math.atan2(deltaY, deltaX);
    }

    private static Vector2d rotate(Vector2d p, float angleRadian, Vector2d center) {
        float cos = (float) cos(angleRadian);
        float sin = (float) sin(angleRadian);

        float x = p.getD0();
        float y = p.getD1();

        float x0 = center.getD0();
        float y0 = center.getD1();

        return new Vector2d(
            x0 + (x - x0) * cos - (y - y0) * sin,
            y0 + (y - y0) * cos + (x - x0) * sin);
    }
}
