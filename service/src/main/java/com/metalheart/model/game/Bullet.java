package com.metalheart.model.game;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"playerId", "id"})
public class Bullet {

    private Long id;
    private String playerId;
    private Instant createdAt;
    private GameObject gameObject;
}
