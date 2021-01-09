package com.metalheart.service.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.StateSnapshot;
import com.metalheart.model.event.ServerTicEvent;
import com.metalheart.service.GameLauncherService;
import com.metalheart.service.GameStateService;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameLauncherServiceImpl implements GameLauncherService {

    private static final int TICK_RATE = 15;
    private static final int TICK_DELAY = 1000 / TICK_RATE;

    private AtomicBoolean running;
    private AtomicLong sequenceNumber;

    private ApplicationEventPublisher applicationEventPublisher;

    private GameStateService stateService;

    public GameLauncherServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                   GameStateService gameStateService) {
        this.running = new AtomicBoolean();
        this.sequenceNumber = new AtomicLong();

        this.applicationEventPublisher = applicationEventPublisher;
        this.stateService = gameStateService;
    }

    @Override
    public void start() {
        running.set(true);
        while (running.get()) {
            try {

                Instant t0 = Instant.now();

                Map<String, PlayerSnapshot> snapshots = stateService.calculateGameState(TICK_DELAY);

                StateSnapshot stateSnapshot = StateSnapshot.builder()
                    .sequenceNumber(sequenceNumber.incrementAndGet())
                    .timestamp(Instant.now().toEpochMilli())
                    .snapshot(snapshots.values().stream().findFirst().orElse(null)) // tmp
                    .build();

                applicationEventPublisher.publishEvent(new ServerTicEvent(stateSnapshot));

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
