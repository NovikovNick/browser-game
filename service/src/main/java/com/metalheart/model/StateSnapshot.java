package com.metalheart.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateSnapshot {

    private long sequenceNumber;
    private long timestamp;
    private PlayerSnapshot snapshot;
}
