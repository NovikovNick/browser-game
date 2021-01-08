package com.metalheart.model;

import lombok.Data;

@Data
public class InputRequest {
    private int mousePosX;
    private int mousePosY;
    private Boolean isPressedW;
    private Boolean isPressedA;
    private Boolean isPressedS;
    private Boolean isPressedD;
}

