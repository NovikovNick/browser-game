package com.metalheart.model;

import com.metalheart.model.game.GameObject;
import com.metalheart.model.game.GameObjectType;
import com.metalheart.model.game.Player;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

public class State implements Cloneable{

    private final Map<Long, GameObject> all;
    private final Map<GameObjectType, Set<GameObject>> groupedByType;

    @Getter
    private final Map<String, Player> players;


    public State(State state) {
        this();
        state.players.forEach((k, v) -> players.put(k, v.clone()));
        state.all.values().stream().map(GameObject::clone).forEach(this::addGameObject);
    }


    public State() {
        all = new HashMap<>();
        players = new HashMap<>();
        groupedByType = new HashMap<>();
    }

    public Collection<GameObject> getAll() {
        return all.values();
    }

    public boolean isPlayerRegistered(String playerId) {
        return players.containsKey(playerId);
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void addPlayer(String sessionId, Player player) {

        players.put(sessionId, player);
        addGameObject(player);
    }

    public void removePlayer(String sessionId) {
        Player player = players.remove(sessionId);
        removeGameObject(player);
    }

    public void addGameObject(GameObject obj) {

        GameObjectType type = obj.getType();

        all.put(obj.getId(), obj);
        groupedByType.putIfAbsent(type, new HashSet<>());
        groupedByType.get(type).add(obj);
    }

    public GameObject getGameObject(Long id) {
        return all.get(id);
    }

    public void removeGameObject(Long id) {
        removeGameObject(all.get(id));
    }

    public void removeGameObject(GameObject obj) {

        if (obj == null) {
            return;
        }

        GameObjectType type = obj.getType();
        all.remove(obj.getId());
        Set<GameObject> groupedByType = this.groupedByType.get(type);
        if (!CollectionUtils.isEmpty(groupedByType)) {
            groupedByType.remove(obj);
        }

    }

    @Override
    public State clone() {
        return new State(this);
    }
}
