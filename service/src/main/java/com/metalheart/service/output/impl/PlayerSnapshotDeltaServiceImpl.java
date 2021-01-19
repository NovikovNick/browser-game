package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import org.springframework.stereotype.Service;

@Service
public class PlayerSnapshotDeltaServiceImpl implements PlayerSnapshotDeltaService {

    @Override
    public PlayerSnapshot calculateDelta(PlayerSnapshot s1, PlayerSnapshot s2) {
        return s1;
    }
}
