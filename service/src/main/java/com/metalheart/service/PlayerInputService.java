package com.metalheart.service;

import com.metalheart.model.PlayerInput;
import java.util.Map;
import java.util.Set;

public interface PlayerInputService {

    void add(String playerId, PlayerInput input);

    Map<String, Set<PlayerInput>> pop();
}
