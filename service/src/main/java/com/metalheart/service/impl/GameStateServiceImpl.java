package com.metalheart.service.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.State;
import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.model.game.RigidBody;
import com.metalheart.model.game.Transform;
import com.metalheart.service.CollisionDetectionService;
import com.metalheart.service.GameObjectService;
import com.metalheart.service.GameStateService;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.PlayerInputService;
import com.metalheart.service.ShapeService;
import com.metalheart.service.UsernameService;
import com.metalheart.service.WallService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.util.stream.Collectors.toSet;

@Service
public class GameStateServiceImpl implements GameStateService {

    private static final float PLAYER_SPEED = 0.5f;
    private static final float BULLET_SPEED = 1.5f;
    private static final Duration BULLET_LIFETIME = Duration.ofSeconds(10);

    private final UsernameService usernameService;
    private final CollisionDetectionService collisionService;
    private final ShapeService shapeService;
    private final GameObjectService gameObjectService;
    private final PlayerInputService playerInputService;
    private final WallService wallService;

    private final AtomicLong projectileSequence;
    private State state;
    private Lock lock;

    public GameStateServiceImpl(UsernameService usernameService,
                                CollisionDetectionService collisionService,
                                ShapeService shapeService,
                                GameObjectService gameObjectService,
                                PlayerInputService playerInputService,
                                WallService wallService) {

        this.usernameService = usernameService;
        this.collisionService = collisionService;
        this.shapeService = shapeService;
        this.gameObjectService = gameObjectService;
        this.playerInputService = playerInputService;
        this.wallService = wallService;

        this.projectileSequence = new AtomicLong();

        this.lock = new ReentrantLock();
        this.state = State.builder()
            .players(new HashMap<>())
            .projectiles(new TreeSet<>(Comparator.comparing(Bullet::getId)))
            .explosions(new ArrayList<>())
            // .walls(this.wallService.generateWalls())
            .walls(Collections.emptyList())
            .build();
    }

    @Override
    public void registerPlayer(String sessionId, String id) {

        Player player = Player.builder()
            .id(id)
            .gameObject(gameObjectService.newGameObject(Vector2d.ZERO_VECTOR, 0, shapeService.playerBoundingBox()))
            .sessionId(sessionId)
            .username(usernameService.generateUsername())
            .build();

        this.lock.lock();
        try {
            this.state.getPlayers().put(sessionId, player);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public String updateUsername(String playerId, String username) {

        username = StringUtils.isEmpty(username) ? usernameService.generateUsername() : username;

        this.lock.lock();
        try {
            Player player = this.state.getPlayers().get(playerId);
            player.setUsername(username);
        } finally {
            this.lock.unlock();
        }

        return username;
    }

    @Override
    public void unregisterPlayer(String playerId) {
        this.lock.lock();
        try {
            this.state.getPlayers().remove(playerId);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public State calculateGameState(Integer tickDelay) {

        Map<String, Set<PlayerInput>> inputs = playerInputService.pop();

        State cloned = null;

        this.lock.lock();
        try {
            Instant now = Instant.now();

            Map<String, Player> players = this.state.getPlayers();
            Set<Bullet> projectiles = this.state.getProjectiles();
            List<Vector2d> explosions = new ArrayList<>();
            List<GameObject> walls = this.state.getWalls();

            for (String sessionId : inputs.keySet()) {
                Set<PlayerInput> in = inputs.get(sessionId);
                int requestCount = in.size();
                for (PlayerInput req : in) {

                    if (players.containsKey(sessionId)) {

                        float angleRadian = req.getRotationAngleRadian();
                        Vector2d direction = Vector2d.ZERO_VECTOR;
                        if (req.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
                        if (req.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);
                        if (req.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
                        if (req.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
                        direction = direction.normalize();
                        direction = GeometryUtil.rotate(direction, angleRadian, Vector2d.ZERO_VECTOR);
                        float magnitude = PLAYER_SPEED * tickDelay / requestCount;
                        Vector2d force = direction.scale(magnitude);

                        Player player = players.get(sessionId);
                        Transform transform = player.getGameObject().getTransform();

                        Vector2d oldCenter = transform.getPosition();
                        Vector2d center = oldCenter.plus(force);

                        RigidBody rigidBody = player.getGameObject().getRigidBody();
                        Polygon2d shape = rigidBody.getShape();
                        Polygon2d transformed = GeometryUtil.rotate(shape.withOffset(center), angleRadian, center);

                        for (Player other : players.values()) {
                            if (!other.equals(player)) {
                                Polygon2d otherTransformed = other.getGameObject().getRigidBody().getTransformed();
                                CollisionResult collision = collisionService.detectCollision(transformed,
                                    otherTransformed);

                                if (collision.isCollide()) {
                                    explosions.add(center);
                                    Vector2d normal = collision.getNormal();
                                    transformed = GeometryUtil.rotate(shape.withOffset(center), -angleRadian, center);
                                    center = center.plus(normal.reversed().scale(collision.getDepth()));
                                }
                            }
                        }

                        for (GameObject wall : walls) {
                            Polygon2d otherTransformed = wall.getRigidBody().getTransformed();
                            CollisionResult collision = collisionService.detectCollision(transformed,
                                otherTransformed);

                            if (collision.isCollide()) {
                                Vector2d normal = collision.getNormal();
                                transformed = GeometryUtil.rotate(shape.withOffset(center), -angleRadian, center);
                                center = center.plus(normal.reversed().scale(collision.getDepth()));
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

                            projectiles.add(Bullet.builder()
                                .id(projectileSequence.incrementAndGet())
                                .playerId(player.getId())
                                .createdAt(now)
                                .gameObject(gameObjectService.newGameObject(center, angleRadian,
                                    shapeService.bulletBoundingBox()))
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

                    GameObject go = gameObjectService.newGameObject(center, angleRadian,
                        shapeService.bulletBoundingBox());

                    for (Player player : players.values()) {
                        Polygon2d playerBox = player.getGameObject().getRigidBody().getTransformed();
                        CollisionResult collision = collisionService.detectCollision(
                            go.getRigidBody().getTransformed(),
                            playerBox);

                        if (collision.isCollide()) {
                            explosions.add(center);
                            return null;
                        }
                    }
                    for (GameObject wall : walls) {
                        Polygon2d otherTransformed = wall.getRigidBody().getTransformed();
                        CollisionResult collision = collisionService.detectCollision(
                            go.getRigidBody().getTransformed(),
                            otherTransformed);

                        if (collision.isCollide()) {
                            return null;
                        }
                    }

                    return Bullet.builder()
                        .playerId(bullet.getPlayerId())
                        .id(bullet.getId())
                        .createdAt(bullet.getCreatedAt())
                        .gameObject(go)
                        .build();
                })
                .filter(Objects::nonNull)
                .collect(toSet());

            this.state = State.builder()
                .players(players)
                .projectiles(projectiles)
                .explosions(explosions)
                .walls(walls)
                .build();
            cloned = this.state.clone();

        } finally {
            this.lock.unlock();
        }
        return cloned;
    }
}
