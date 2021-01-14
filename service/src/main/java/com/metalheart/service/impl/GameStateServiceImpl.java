package com.metalheart.service.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.service.CollisionDetectionService;
import com.metalheart.service.GameStateService;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.ShapeService;
import com.metalheart.service.UsernameService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

@Service
public class GameStateServiceImpl implements GameStateService {

    private static final float PLAYER_SPEED = 0.5f;
    private static final float BULLET_SPEED = 1.5f;
    private static final Duration BULLET_LIFETIME = Duration.ofSeconds(10);

    private UsernameService usernameService;
    private CollisionDetectionService collisionService;
    private ShapeService shapeService;

    private final Map<String, Player> players;
    private final AtomicLong projectileSequence;
    private Set<Bullet> projectiles;

    private final Map<String, Set<PlayerInput>> inputs;
    public static final Vector2d CENTER = Vector2d.of(900, 450);


    public GameStateServiceImpl(UsernameService usernameService,
                                CollisionDetectionService collisionService,
                                ShapeService shapeService) {

        this.usernameService = usernameService;
        this.collisionService = collisionService;
        this.shapeService = shapeService;
        this.players = new ConcurrentHashMap<>();

        this.inputs = new ConcurrentHashMap<>();

        projectileSequence = new AtomicLong();
        this.projectiles = new TreeSet<>(Comparator.comparing(Bullet::getId));
    }

    @Override
    public void registerPlayer(String sessionId, String id) {

        Player player = Player.builder()
            .id(id)
            .gameObject(GameObject.builder()
                .transform(Transform.builder()
                    .position(Vector2d.ZERO_VECTOR)
                    .rotationAngleRadian(0)
                    .build())
                .rigidBody(RigidBody.builder()
                    .shape(shapeService.playerBoundingBox())
                    .transformed(shapeService.playerBoundingBox())
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

        List<Vector2d> explosions = new ArrayList<>();
        Instant now = Instant.now();

        for (String sessionId : inputs.keySet()) {
            Set<PlayerInput> in = inputs.remove(sessionId);
            int requestCount = in.size();
            for (PlayerInput req : in) {

                if (players.containsKey(sessionId)) {

                    Player player = players.get(sessionId);

                    float angleRadian = req.getRotationAngleRadian();

                    Vector2d direction = Vector2d.ZERO_VECTOR;
                    if (req.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
                    if (req.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);
                    if (req.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
                    if (req.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
                    direction = direction.normalize();

                    Transform transform = player.getGameObject().getTransform();
                    direction = GeometryUtil.rotate(direction, angleRadian, Vector2d.ZERO_VECTOR);

                    float magnitude = PLAYER_SPEED * tickDelay / requestCount;
                    Vector2d force = direction.scale(magnitude);

                    Vector2d oldCenter = transform.getPosition();
                    Vector2d center = oldCenter.plus(force);

                    RigidBody rigidBody = player.getGameObject().getRigidBody();
                    Polygon2d shape = rigidBody.getShape();
                    Polygon2d transformed = GeometryUtil.rotate(shape.withOffset(center), angleRadian, center);

                    for (Player other : players.values()) {
                        if (!other.equals(player)) {
                            Polygon2d otherTransformed = other.getGameObject().getRigidBody().getTransformed();
                            CollisionResult collision = collisionService.detectCollision(transformed, otherTransformed);

                            if (collision.isCollide()) {
                                explosions.add(center);
                                Vector2d normal = collision.getNormal();
                                transformed = GeometryUtil.rotate(shape.withOffset(center), -angleRadian, center);
                                center = center.plus(normal.reversed().scale(collision.getDepth()));
                            }
                        }
                    }

                    player.setGameObject(GameObject.builder()
                        .transform(Transform.builder()
                            .position(center)
                            .rotationAngleRadian(angleRadian)
                            .build())
                        .rigidBody(RigidBody.builder()
                            .shape(shape)
                            .transformed(transformed)
                            .build())
                        .build());

                    if (req.getLeftBtnClicked()) {

                        Polygon2d bullet = shapeService.bulletBoundingBox();

                        projectiles.add(Bullet.builder()
                            .id(projectileSequence.incrementAndGet())
                            .playerId(player.getId())
                            .createdAt(now)
                            .gameObject(GameObject.builder()
                                .transform(Transform.builder()
                                    .position(center)
                                    .rotationAngleRadian(angleRadian)
                                    .build())
                                .rigidBody(RigidBody.builder()
                                    .shape(bullet)
                                    .transformed(GeometryUtil.rotate(bullet.withOffset(center), angleRadian, center))
                                    .build())
                                .build())
                            .build());
                    }
                }
            }
        }

        projectiles = projectiles.stream()
            .map(bullet -> {
                float angleRadian = bullet.getGameObject().getTransform().getRotationAngleRadian();
                Vector2d oldCenter = bullet.getGameObject().getTransform().getPosition();


                Vector2d direction = Vector2d.UNIT_VECTOR_D0.reversed();
                direction = GeometryUtil.rotate(direction, angleRadian, Vector2d.ZERO_VECTOR);

                Vector2d delta = direction.scale(BULLET_SPEED * tickDelay);
                Vector2d center = oldCenter.plus(delta);

                if (bullet.getCreatedAt().plus(BULLET_LIFETIME).isBefore(now)) {
                    return null;
                }

                Polygon2d transformed = GeometryUtil.rotate(shapeService.bulletBoundingBox().withOffset(center),
                    angleRadian, center);

                for (Player player : players.values()) {
                    Polygon2d playerBox = player.getGameObject().getRigidBody().getTransformed();
                    CollisionResult collision = collisionService.detectCollision(transformed, playerBox);

                    if (collision.isCollide()) {
                        explosions.add(center);
                        return null;
                    }
                }

                return Bullet.builder()
                    .playerId(bullet.getPlayerId())
                    .id(bullet.getId())
                    .createdAt(bullet.getCreatedAt())
                    .gameObject(GameObject.builder()
                        .transform(Transform.builder()
                            .position(center)
                            .rotationAngleRadian(angleRadian)
                            .build())
                        .rigidBody(RigidBody.builder()
                            .shape(shapeService.bulletBoundingBox())
                            .transformed(transformed)
                            .build())
                        .build())
                    .build();
            })
            .filter(Objects::nonNull)
            .collect(toSet());

        Map<String, PlayerSnapshot> snapshots = new HashMap<>();
        players.forEach((id, player) -> {

            Player cloned = player.clone();

            Vector2d playerPosition = cloned.getGameObject().getTransform().getPosition();

            Vector2d direction = playerPosition.reversed().plus(CENTER);

            RigidBody rigidBody = cloned.getGameObject().getRigidBody();

            GameObject gameObject = GameObject.builder()
                .transform(Transform.builder()
                    .position(CENTER)
                    .rotationAngleRadian(cloned.getGameObject().getTransform().getRotationAngleRadian())
                    .build())
                .rigidBody(RigidBody.builder()
                    .shape(rigidBody.getShape())
                    .transformed(rigidBody.getTransformed().withOffset(direction))
                    .build())
                .build();
            cloned.setGameObject(gameObject);

            List<Player> enemies = players.values().stream()
                .filter(enemy -> !player.equals(enemy))
                .map(enemy -> {
                    return Player.builder()
                        .id(enemy.getId())
                        .sessionId(enemy.getSessionId())
                        .username(enemy.getUsername())
                        .gameObject(GameObject.builder()
                            .transform(Transform.builder()
                                .position(enemy.getGameObject().getTransform().getPosition().plus(direction))
                                .rotationAngleRadian(enemy.getGameObject().getTransform().getRotationAngleRadian())
                                .build())
                            .rigidBody(RigidBody.builder()
                                .shape(enemy.getGameObject().getRigidBody().getShape())
                                .transformed(enemy.getGameObject().getRigidBody().getTransformed().withOffset(direction))
                                .build())
                            .build())
                        .build();
                })
                .collect(Collectors.toList());

            PlayerSnapshot snapshot = PlayerSnapshot.builder()
                .character(cloned)
                .enemies(enemies)
                .projectiles(projectiles.stream().map(p -> Bullet.builder()
                    .id(p.getId())
                    .playerId(p.getPlayerId())
                    .gameObject(GameObject.builder()
                        .transform(Transform.builder()
                            .position(p.getGameObject().getTransform().getPosition().plus(direction))
                            .rotationAngleRadian(p.getGameObject().getTransform().getRotationAngleRadian())
                            .build())
                        .rigidBody(RigidBody.builder()
                            .shape(p.getGameObject().getRigidBody().getShape())
                            .transformed(p.getGameObject().getRigidBody().getTransformed().withOffset(direction))
                            .build())
                        .build())
                    .build()).collect(Collectors.toSet()))
                .explosions(explosions.stream().map(p -> p.plus(direction)).collect(Collectors.toList()))
                .walls(asList(shapeService.wallBoundingBox().withOffset(Vector2d.of(100, 100)).withOffset(direction)))
                .build();
            snapshots.put(id, snapshot);
        });

        return snapshots;
    }
}
