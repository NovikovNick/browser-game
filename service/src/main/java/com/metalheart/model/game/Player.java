package com.metalheart.model.game;

import com.metalheart.service.tmp.Body;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"sessionId", "id"})
public class Player implements Cloneable {

    private String sessionId;
    private String username;
    private String id;

    private Body gameObject;

    @Override
    public Player clone() {
        return Player.builder()
            .id(id)
            .sessionId(sessionId)
            .username(username)
            .gameObject(gameObject)
            .build();
    }
}
