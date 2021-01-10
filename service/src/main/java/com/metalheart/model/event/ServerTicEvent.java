package com.metalheart.model.event;

import com.metalheart.model.StateSnapshot;
import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


public class ServerTicEvent extends ApplicationEvent {

    private static final long serialVersionUID = 8419098254360491811L;

    @Getter
    private final Map<String, StateSnapshot> snapshots;


    public ServerTicEvent(Map<String, StateSnapshot> snapshots) {
        super(snapshots);
        this.snapshots = snapshots;
    }
}

