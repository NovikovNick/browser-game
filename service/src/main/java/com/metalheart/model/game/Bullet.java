package com.metalheart.model.game;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"playerId", "id"})
public class Bullet {

    private Long id;
    private String playerId;
    private GameObject gameObject;
}
