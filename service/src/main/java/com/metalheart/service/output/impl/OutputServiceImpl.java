package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import com.metalheart.model.State;
import com.metalheart.service.output.OutputService;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import com.metalheart.service.output.PlayerSnapshotService;
import com.metalheart.service.state.PlayerPresentationService;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class OutputServiceImpl implements OutputService {

    private final AtomicLong sequenceNumber;

    private final PlayerSnapshotService projectionService;
    private final PlayerSnapshotDeltaService snapshotService;
    private final PlayerPresentationService presentationService;

    public OutputServiceImpl(PlayerSnapshotService playerSnapshotService,
                             PlayerSnapshotDeltaService deltaService,
                             PlayerPresentationService playerPresentationService) {
        this.sequenceNumber = new AtomicLong();
        this.projectionService = playerSnapshotService;
        this.snapshotService = deltaService;
        this.presentationService = playerPresentationService;
    }

    @Override
    public Map<String, PlayerSnapshot> toSnapshots(State state) {

        long timestamp = Instant.now().toEpochMilli();
        long sequenceNumber = this.sequenceNumber.incrementAndGet();
        Map<String, PlayerSnapshot> res = new HashMap<>();

        projectionService.splitState(state).forEach((playerId, projection) -> {

            PlayerStatePresentation presentation = presentationService.getPlayerStatePresentation(playerId);

            PlayerSnapshot snapshot = snapshotService.getDelta(presentation, projection);
            snapshot.setSequenceNumber(sequenceNumber);
            snapshot.setTimestamp(timestamp);

            presentationService.saveSnapshot(playerId, snapshot);

            res.put(playerId, snapshot);
        });

        return res;
    }
}
