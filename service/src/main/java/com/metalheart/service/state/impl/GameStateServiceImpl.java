package com.metalheart.service.state.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.State;
import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Manifold;
import com.metalheart.model.common.Vector2d;
import com.metalheart.model.game.Bullet;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.service.GeometryUtil;
import com.metalheart.service.input.PlayerInputService;
import com.metalheart.service.physic.CollisionDetector;
import com.metalheart.service.physic.CollisionResolver;
import com.metalheart.service.state.GameObjectService;
import com.metalheart.service.state.GameStateService;
import com.metalheart.service.state.PlayerPresentationService;
import com.metalheart.service.state.UsernameService;
import com.metalheart.service.state.WallService;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.metalheart.model.game.GameObjectType.PLAYER;

@Service
public class GameStateServiceImpl implements GameStateService {

    private static final float PLAYER_SPEED = 20f;
    private static final float BULLET_SPEED = 15f;
    private static final Duration BULLET_LIFETIME = Duration.ofSeconds(10);

    private final UsernameService usernameService;
    private final CollisionDetector collisionService;
    private final CollisionResolver collisionResolver;
    private final GameObjectService gameObjectService;
    private final PlayerInputService playerInputService;
    private final WallService wallService;
    private final PlayerPresentationService playerPresentationService;

    private State state;
    private Lock lock;

    public GameStateServiceImpl(UsernameService usernameService,
                                CollisionDetector collisionService,
                                CollisionResolver collisionResolver,
                                GameObjectService gameObjectService,
                                PlayerInputService playerInputService,
                                WallService wallService,
                                PlayerPresentationService playerPresentationService) {

        this.usernameService = usernameService;
        this.collisionService = collisionService;
        this.collisionResolver = collisionResolver;
        this.gameObjectService = gameObjectService;
        this.playerInputService = playerInputService;
        this.wallService = wallService;
        this.playerPresentationService = playerPresentationService;

        this.lock = new ReentrantLock();

        this.state = new State();
        this.wallService.generateMaze().stream()
            .map(pos -> gameObjectService.newWall(pos, 0))
            .forEach(state::addGameObject);
    }

    @Override
    public void registerPlayer(String sessionId, String id) {
        Player player = gameObjectService.newPlayer(Vector2d.of(1, 1), 0);
        player.setSessionId(sessionId);
        player.setUsername(id);

        this.lock.lock();
        try {
            this.state.addPlayer(sessionId, player);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public String updateUsername(String playerId, String username) {

        username = StringUtils.isEmpty(username) ? usernameService.generateUsername() : username;

        this.lock.lock();
        try {
            // todo use 'id' to send events to player
            // this.state.getPlayer(playerId).setUsername(username);
        } finally {
            this.lock.unlock();
        }

        return username;
    }

    @Override
    public void unregisterPlayer(String playerId) {
        this.lock.lock();
        try {
            this.state.removePlayer(playerId);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public State step(float dt) {

        Map<String, List<PlayerInput>> inputs = playerInputService.pop();
        State state = null;

        this.lock.lock();
        try {
            Instant now = Instant.now();

            state = this.state.clone();


            // confirm previous sent snapshots
            for (String sessionId : inputs.keySet()) {

                List<PlayerInput> in = inputs.get(sessionId);
                Set<Long> ack = in.stream()
                    .map(PlayerInput::getAckSN)
                    .filter(Objects::nonNull)
                    .flatMap(id -> id.stream())
                    .collect(Collectors.toSet());

                playerPresentationService.confirmSnapshots(sessionId, ack);
            }

            // apply forces
            for (String sessionId : inputs.keySet()) {

                List<PlayerInput> in = inputs.get(sessionId);
                int requestCount = in.size();

                for (PlayerInput req : in) {

                    if (state.isPlayerRegistered(sessionId)) {

                        float magnitude = PLAYER_SPEED / requestCount;
                        Vector2d force = getForceDirection(req).scale(magnitude);

                        Player player = state.getPlayer(sessionId);
                        player.setForce(player.getForce().plus(force));

                        if (req.getLeftBtnClicked()) {

                            Vector2d bulletDir = GeometryUtil.rotate(Vector2d.UNIT_VECTOR_D0.reversed(),
                                req.getRotationAngleRadian(),
                                Vector2d.ZERO_VECTOR);
                            Bullet bullet = gameObjectService.newBullet(player.getPos(), req.getRotationAngleRadian());
                            bullet.setPlayerId(player.getId());
                            bullet.setCreatedAt(now);
                            bullet.setVelocity(bulletDir.scale(BULLET_SPEED));

                            state.addGameObject(bullet);
                        }
                    }
                }
            }

            Collection<GameObject> bodies = state.getAll();
            for (GameObject body : bodies) {

                if (body.getMass() != 0) {
                    // body.setForce(body.getForce().plus(Vector2d.UNIT_VECTOR_D1.scale(10f)));
                }
            }

            // integrate
            for (GameObject body : bodies) {
                Vector2d scaledForce = body.getForce().scale(body.getInvMass() * dt);
                body.setVelocity(body.getVelocity().plus(scaledForce));
                body.setPos(body.getPos().plus(body.getVelocity()));
                body.setForce(Vector2d.ZERO_VECTOR);
            }

            // resolve collision
            Set<Manifold> manifolds = collisionService.findCollision(bodies);

            for (Manifold manifold : manifolds) {

                GameObject a = manifold.getA();
                GameObject b = manifold.getB();

                if (PLAYER.equals(a.getType())) {

                    Vector2d pos = AABB2d.of(a.getPos(), b.getPos()).getCenter();
                    // state.addGameObject(gameObjectService.newExplosion(pos, 0));
                }
            }
            collisionResolver.resolve(manifolds);


            this.state = state.clone();

        } finally {
            this.lock.unlock();
        }
        return state;
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
