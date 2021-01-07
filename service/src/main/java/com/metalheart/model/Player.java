package com.metalheart.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Player {

    private String id;
    private String username;
    private int x;
    private int y;
}
