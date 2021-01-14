package com.metalheart.model;

import java.time.Instant;
import lombok.Data;

@Data
public class PlayerInput {
    private float rotationAngleRadian;
    private Boolean isPressedW;
    private Boolean isPressedA;
    private Boolean isPressedS;
    private Boolean isPressedD;
    private Boolean leftBtnClicked;
    private Boolean rightBtnClicked;

    private Instant time;
}
