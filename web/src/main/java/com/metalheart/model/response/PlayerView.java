package com.metalheart.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerView {
    private String username;
    private GameObjectView obj;
}

