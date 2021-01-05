package com.metalheart.model;

import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


public class ServerTicEvent extends ApplicationEvent {

    private static final long serialVersionUID = 8419098254360491811L;

    @Getter
    private final Map<String, Point2d> players;


    public ServerTicEvent(Map<String, Point2d> players) {
        super(players);
        this.players = players;
    }
}

