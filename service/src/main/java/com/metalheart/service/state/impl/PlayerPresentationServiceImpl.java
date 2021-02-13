package com.metalheart.service.state.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.Player;
import com.metalheart.service.state.PlayerPresentationService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class PlayerPresentationServiceImpl implements PlayerPresentationService {


    private final Map<String, PlayerStatePresentation> playerPresentations;
    private final Map<String, Map<Long, PlayerSnapshot>> unconfirmedDeltas;
    private ReentrantLock lock;

    public PlayerPresentationServiceImpl() {
        this.playerPresentations = new HashMap<>();
        this.unconfirmedDeltas = new HashMap<>();
        this.lock = new ReentrantLock();
    }


    @Override
    public PlayerStatePresentation getPlayerStatePresentation(String playerId) {
        playerPresentations.putIfAbsent(playerId, new PlayerStatePresentation());
        return playerPresentations.get(playerId);
    }

    @Override
    public void saveSnapshot(String playerId, PlayerSnapshot snapshot) {
        lock.lock();
        try {

            Map<Long, PlayerSnapshot> playerUnconfirmedDeltas = unconfirmedDeltas.get(playerId);
            if (playerUnconfirmedDeltas == null || playerUnconfirmedDeltas.size() > 100) {
                playerUnconfirmedDeltas = new HashMap<>();
                unconfirmedDeltas.put(playerId, playerUnconfirmedDeltas);
            }

            playerUnconfirmedDeltas.put(snapshot.getSequenceNumber(), snapshot);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void confirmSnapshots(String playerId, Set<Long> askSN) {

        lock.lock();
        try {
            PlayerStatePresentation presentation = getPlayerStatePresentation(playerId);

            unconfirmedDeltas.putIfAbsent(playerId, new HashMap<>());
            Map<Long, PlayerSnapshot> playerUnconfirmedDeltas = unconfirmedDeltas.get(playerId);

            askSN.stream()
                .sorted()
                .map(playerUnconfirmedDeltas::remove)
                .filter(Objects::nonNull)
                .forEach(snapshot -> {

                    for (String removedId : snapshot.getRemoved()) {
                        presentation.removeGameObject(Long.valueOf(removedId));
                    }

                    if (Objects.nonNull(snapshot.getCharacter())) {
                        presentation.setPlayer(snapshot.getCharacter().clone());
                    }

                    if (!isEmpty(snapshot.getEnemies())) {
                        snapshot.getEnemies().stream().map(Player::clone).forEach(presentation::addEnemy);
                    }
                    if (!isEmpty(snapshot.getWalls())) {
                        snapshot.getWalls().stream().map(GameObject::clone).forEach(presentation::addGameObject);
                    }
                    if (!isEmpty(snapshot.getExplosions())) {
                        snapshot.getExplosions().stream().map(GameObject::clone).forEach(presentation::addGameObject);
                    }
                    if (!isEmpty(snapshot.getProjectiles())) {
                        snapshot.getProjectiles().stream().map(GameObject::clone).forEach(presentation::addGameObject);
                    }
                });
        } finally {
            lock.unlock();
        }

    }

    private PlayerStatePresentation getPresentation(String playerId) {
        return playerPresentations.get(playerId);
    }
}
