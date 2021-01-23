package com.metalheart.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameObjectView {
    private String id;
    private float[] pos;
    private float rot;
}

