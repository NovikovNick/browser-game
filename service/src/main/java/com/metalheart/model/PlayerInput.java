package com.metalheart.model;

import java.time.Instant;
import lombok.Data;

@Data
public class PlayerInput {
    private int mousePosX;
    private int mousePosY;
    private Boolean isPressedW;
    private Boolean isPressedA;
    private Boolean isPressedS;
    private Boolean isPressedD;

    private Instant time;
}
