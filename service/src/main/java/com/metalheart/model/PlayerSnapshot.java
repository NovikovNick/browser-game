package com.metalheart.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "sessionId")
public class PlayerSnapshot {

    private String sessionId;

    private String username;

    private Integer mousePosX;
    private Integer mousePosY;

    private Integer characterPosX;
    private Integer characterPosY;
}
