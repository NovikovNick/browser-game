package com.metalheart.model;

import lombok.Data;

@Data
public class InputRequest {
    private Long ackSN;
    private float rotationAngleRadian;
    private Boolean isPressedW;
    private Boolean isPressedA;
    private Boolean isPressedS;
    private Boolean isPressedD;
    private Boolean leftBtnClicked;
    private Boolean rightBtnClicked;
}

