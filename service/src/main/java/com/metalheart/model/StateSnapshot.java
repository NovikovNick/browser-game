package com.metalheart.model;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateSnapshot {

    private long sequenceNumber;
    private long timestamp;
    private Set<PlayerSnapshot> players;
}
