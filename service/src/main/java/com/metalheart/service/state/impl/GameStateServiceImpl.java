package com.metalheart.service.state.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.State;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.Player;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.input.PlayerInputService;
import com.metalheart.service.state.GameObjectService;
import com.metalheart.service.state.GameStateService;
import com.metalheart.service.state.ShapeService;
import com.metalheart.service.state.UsernameService;
import com.metalheart.service.state.WallService;
import com.metalheart.service.tmp.Body;
import com.metalheart.service.tmp.CollisionDetector;
import com.metalheart.service.tmp.CollisionResolver;
import com.metalheart.service.tmp.Manifold;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GameStateServiceImpl implements GameStateService {

    private static final float PLAYER_SPEED = 0.05f;
    private static final float BULLET_SPEED = 1.5f;
    private static final Duration BULLET_LIFETIME = Duration.ofSeconds(10);

    private final UsernameService usernameService;
    private final CollisionDetector collisionService;
    private final CollisionResolver collisionResolver;
    private final ShapeService shapeService;
    private final GameObjectService gameObjectService;
    private final PlayerInputService playerInputService;
    private final WallService wallService;

    private final AtomicLong projectileSequence;
    private State state;
    private Lock lock;

    public GameStateServiceImpl(UsernameService usernameService,
                                CollisionDetector collisionService,
                                CollisionResolver collisionResolver,
                                ShapeService shapeService,
                                GameObjectService gameObjectService,
                                PlayerInputService playerInputService,
                                WallService wallService) {

        this.usernameService = usernameService;
        this.collisionService = collisionService;
        this.collisionResolver = collisionResolver;
        this.shapeService = shapeService;
        this.gameObjectService = gameObjectService;
        this.playerInputService = playerInputService;
        this.wallService = wallService;

        this.projectileSequence = new AtomicLong();

        this.lock = new ReentrantLock();
        List<Vector2d> wallPositions = this.wallService.generateMaze();
        this.state = State.builder()
            .playersAckSN(new HashMap<>())
            .players(new HashMap<>())
            .projectiles(new TreeSet<>(Comparator.comparing(Bullet::getId)))
            .explosions(Collections.emptyList())
            .walls(wallPositions.stream()
                .map(pos -> gameObjectService.newWall(pos, 0))
                .collect(Collectors.toList()))
            .removedGameObjectIds(Collections.emptyList())
            .build();
    }

    @Override
    public void registerPlayer(String sessionId, String id) {

        Player player = Player.builder()
            .id(id)
            .gameObject(gameObjectService.newPlayer(Vector2d.of(100, 100), 0))
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

        Map<String, List<PlayerInput>> inputs = playerInputService.pop();

        State cloned = null;

        this.lock.lock();
        try {
            Instant now = Instant.now();

            Map<String, Player> players = this.state.getPlayers();
            final List<Body> walls = this.state.getWalls();
            Set<Bullet> projectiles = this.state.getProjectiles();
            List<Vector2d> explosions = new ArrayList<>();
            List<String> removedGameObjectIds = new ArrayList<>();

            Map<String, Long> ackSN = this.state.getPlayersAckSN();

            // apply forces
            for (String sessionId : inputs.keySet()) {
                List<PlayerInput> in = inputs.get(sessionId);
                int requestCount = in.size();
                for (PlayerInput req : in) {

                    ackSN.merge(sessionId, 0l,
                        (old, v) -> old != null &&  req.getAckSN() != null && old > req.getAckSN()
                            ? old
                            : req.getAckSN());

                    if (players.containsKey(sessionId)) {

                        float magnitude = PLAYER_SPEED * tickDelay / requestCount;
                        Vector2d force = getForceDirection(req).scale(magnitude);

                        Player player = players.get(sessionId);
                        player.getGameObject().setForce(player.getGameObject().getForce().plus(force));

                        /*if (req.getLeftBtnClicked()) {

                            projectiles.add(Bullet.builder()
                                .id(projectileSequence.incrementAndGet())
                                .playerId(player.getId())
                                .createdAt(now)
                                .gameObject(gameObjectService.newGameObject(center, angleRadian,
                                    shapeService.bulletShape()))
                                .build());
                        }*/
                    }
                }
            }

            // integrate
            List<Body> bodies = players.values().stream()
                .map(Player::getGameObject)
                .collect(Collectors.toList());
            bodies.addAll(walls);
            for (Body body : bodies) {
                if (body.getMass() != 0) {
                    // body.setForce(body.getForce().plus(Vector2d.UNIT_VECTOR_D1.scale(GRAVITY)));
                }
                body.setVelocity(body.getVelocity().plus(body.getForce().scale(body.getInvMass() * tickDelay)));
                body.setPos(body.getPos().plus(body.getVelocity()));
                body.setForce(Vector2d.ZERO_VECTOR);
            }

            // resolve collision
            Set<Manifold> manifolds = collisionService.findCollision(bodies);
            collisionResolver.resolve(manifolds);

            /*projectiles = projectiles.stream()
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

                    GameObject go = gameObjectService.transform(bullet.getGameObject(), center, angleRadian);

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
                .collect(toSet());*/

            Map<String, Long> ack = new HashMap<>();
            ackSN.forEach((sessionId, n) -> {
                if (players.keySet().contains(sessionId)) {
                    ack.put(sessionId, n);
                }
            });
            this.state = State.builder()
                .playersAckSN(ack)
                .players(players)
                .projectiles(projectiles)
                .explosions(explosions)
                .walls(walls)
                .removedGameObjectIds(removedGameObjectIds)
                .build();
            cloned = this.state.clone();

        } finally {
            this.lock.unlock();
        }
        return cloned;
    }

    private Vector2d getForceDirection(PlayerInput req) {
        Vector2d direction = Vector2d.ZERO_VECTOR;
        float angleRadian = req.getRotationAngleRadian();
        if (req.getIsPressedW()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0.reversed());
        if (req.getIsPressedS()) direction = direction.plus(Vector2d.UNIT_VECTOR_D0);
        if (req.getIsPressedA()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1);
        if (req.getIsPressedD()) direction = direction.plus(Vector2d.UNIT_VECTOR_D1.reversed());
        direction = direction.normalize();
        direction = GeometryUtil.rotate(direction, angleRadian, Vector2d.ZERO_VECTOR);
        return direction;
    }
}
