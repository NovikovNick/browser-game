package com.metalheart.model;

import lombok.Data;

@Data
public class ChangePlayerNameRequest {

    private String sessionId;
    private String username;
}
