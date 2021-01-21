package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.StateSnapshot;
import com.metalheart.model.struct.LimitedList;
import com.metalheart.service.output.OutputService;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import com.metalheart.service.output.PlayerSnapshotService;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class OutputServiceImpl implements OutputService {

    private AtomicLong sequenceNumber;

    private static int PLAYER_SNAPSHOT_SIZE = 32;
    private Lock lock;

    private final PlayerSnapshotService playerSnapshotService;
    private final PlayerSnapshotDeltaService deltaService;
    private Map<String, LimitedList<Snapshot>> snapshots;

    public OutputServiceImpl(PlayerSnapshotService playerSnapshotService,
                             PlayerSnapshotDeltaService deltaService) {
        this.sequenceNumber = new AtomicLong();
        this.playerSnapshotService = playerSnapshotService;
        this.deltaService = deltaService;
        this.snapshots = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public Map<String, StateSnapshot> toSnapshots(State state) {

        long timestamp = Instant.now().toEpochMilli();
        long sequenceNumber = this.sequenceNumber.incrementAndGet();
        Map<String, Long> ackSN = state.getPlayersAckSN();
        Map<String, PlayerSnapshot> playerToSnapshots = playerSnapshotService.splitState(state);
        Map<String, StateSnapshot> res = new HashMap<>();

        lock.lock();
        try {
            playerToSnapshots.forEach((sessionId, snapshot) -> {

                snapshots.putIfAbsent(sessionId, new LimitedList<>(PLAYER_SNAPSHOT_SIZE));
                LimitedList<Snapshot> playerStates = snapshots.get(sessionId);
                playerStates.add(Snapshot.builder()
                    .sequenceNumber(sequenceNumber)
                    .ack(false)
                    .snapshot(snapshot)
                    .build());

                Long ack = ackSN.get(sessionId);

                Snapshot ackSnapshot = null;
                if (ack != null) {

                    List<Snapshot> sentSnapshots = playerStates.pollAll()
                        .stream()
                        .sorted(Comparator.comparingLong(Snapshot::getSequenceNumber).reversed())
                        .collect(Collectors.toList());
                    for (Snapshot sentSnapshot : sentSnapshots) {

                        if (sentSnapshot.getSequenceNumber() <= ack) {
                            sentSnapshot.setAck(true);
                            if (ackSnapshot == null || (sentSnapshot.getSequenceNumber() > ackSnapshot.getSequenceNumber())) {
                                ackSnapshot = sentSnapshot;
                            }
                        } else {
                            playerStates.add(sentSnapshot);
                        }
                    }
                }
                playerStates.add(ackSnapshot);

                PlayerSnapshot delta = deltaService.calculateDelta(
                    ackSnapshot == null ? null : ackSnapshot.getSnapshot(),
                    playerStates.toList().stream().map(Snapshot::getSnapshot).collect(Collectors.toList()));

                StateSnapshot stateSnapshot = StateSnapshot.builder()
                    .sequenceNumber(sequenceNumber)
                    .timestamp(timestamp)
                    .snapshot(delta)
                    .build();
                res.put(sessionId, stateSnapshot);
            });

        } finally {
            lock.unlock();
        }

        return res;
    }

    @Data
    @Builder
    public static class Snapshot {

        private long sequenceNumber;
        private boolean ack;
        private PlayerSnapshot snapshot;

        @Override
        public String toString() {
            return (ack ? "+" : "-") + "[sn=" + sequenceNumber + "]";
        }
    }
}
