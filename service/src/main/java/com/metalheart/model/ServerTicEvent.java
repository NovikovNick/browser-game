package com.metalheart.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


public class ServerTicEvent extends ApplicationEvent {

    private static final long serialVersionUID = 8419098254360491811L;

    @Getter
    private final StateSnapshot snapshot;


    public ServerTicEvent(StateSnapshot snapshot) {
        super(snapshot);
        this.snapshot = snapshot;
    }
}

