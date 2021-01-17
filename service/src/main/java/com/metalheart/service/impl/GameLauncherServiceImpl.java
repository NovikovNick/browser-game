package com.metalheart.service.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.StateSnapshot;
import com.metalheart.model.event.ServerTicEvent;
import com.metalheart.service.GameLauncherService;
import com.metalheart.service.GameStateService;
import com.metalheart.service.PlayerSnapshotService;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameLauncherServiceImpl implements GameLauncherService {

    private static final int TICK_RATE = 15;
    private static final int TICK_DELAY = 1000 / TICK_RATE;

    private AtomicBoolean running;
    private AtomicLong sequenceNumber;

    private ApplicationEventPublisher applicationEventPublisher;

    private final GameStateService stateService;
    private final PlayerSnapshotService playerSnapshotService;

    public GameLauncherServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                   GameStateService gameStateService,
                                   PlayerSnapshotService playerSnapshotService) {

        this.running = new AtomicBoolean();
        this.sequenceNumber = new AtomicLong();

        this.applicationEventPublisher = applicationEventPublisher;
        this.stateService = gameStateService;
        this.playerSnapshotService = playerSnapshotService;
    }

    @Override
    public void start() {
        running.set(true);
        while (running.get()) {
            try {

                Instant t0 = Instant.now();

                long sequenceNumber = this.sequenceNumber.incrementAndGet();
                long timestamp = t0.toEpochMilli();

                State state = stateService.calculateGameState(TICK_DELAY);
                Map<String, PlayerSnapshot> playerSnapshots = playerSnapshotService.splitState(state);

                Map<String, StateSnapshot> snapshots = playerSnapshots.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> StateSnapshot.builder()
                        .sequenceNumber(sequenceNumber)
                        .timestamp(timestamp)
                        .snapshot(entry.getValue())
                        .build()));

                applicationEventPublisher.publishEvent(new ServerTicEvent(snapshots));

                long calculationTime = Instant.now().minusMillis(t0.toEpochMilli()).toEpochMilli();
                TimeUnit.MILLISECONDS.sleep(TICK_DELAY - calculationTime);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        running.set(false);
    }
}
