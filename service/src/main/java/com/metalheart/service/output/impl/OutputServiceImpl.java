package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.StateSnapshot;
import com.metalheart.model.struct.LimitedList;
import com.metalheart.service.output.OutputService;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import com.metalheart.service.output.PlayerSnapshotService;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

@Service
public class OutputServiceImpl implements OutputService {

    private AtomicLong sequenceNumber;

    private static int PLAYER_SNAPSHOT_SIZE = 32;
    private Lock lock;

    private final PlayerSnapshotService playerSnapshotService;
    private final PlayerSnapshotDeltaService deltaService;
    private Map<String, LimitedList<PlayerSnapshot>> snapshots;

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

        Map<String, PlayerSnapshot> playerToSnapshots = playerSnapshotService.splitState(state);
        Map<String, StateSnapshot> res = new HashMap<>();

        lock.lock();
        try {
            playerToSnapshots.forEach((sessionId, snapshot) -> {
                snapshots.putIfAbsent(sessionId, new LimitedList<>(PLAYER_SNAPSHOT_SIZE));
                LimitedList<PlayerSnapshot> playerSnapshots = snapshots.get(sessionId);
                playerSnapshots.add(snapshot);

                PlayerSnapshot playerSnapshot = null;
                List<PlayerSnapshot> list = playerSnapshots.toList();
                for (PlayerSnapshot s1 : list) {
                    playerSnapshot = deltaService.calculateDelta(s1, playerSnapshot);
                }

                res.put(sessionId, StateSnapshot.builder()
                    .sequenceNumber(sequenceNumber)
                    .timestamp(timestamp)
                    .snapshot(snapshot)
                    .build());
            });

        } finally {
            lock.unlock();
        }

        return res;
    }
}
