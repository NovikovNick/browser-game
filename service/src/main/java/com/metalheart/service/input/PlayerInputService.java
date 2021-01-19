package com.metalheart.service.input;

import com.metalheart.model.PlayerInput;
import java.util.List;
import java.util.Map;

public interface PlayerInputService {

    void add(String playerId, PlayerInput input);

    Map<String, List<PlayerInput>> pop();
}
