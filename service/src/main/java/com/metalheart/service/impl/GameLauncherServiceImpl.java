package com.metalheart.service.impl;

import com.metalheart.model.State;
import com.metalheart.model.StateSnapshot;
import com.metalheart.model.event.ServerTicEvent;
import com.metalheart.service.GameLauncherService;
import com.metalheart.service.output.OutputService;
import com.metalheart.service.state.GameStateService;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameLauncherServiceImpl implements GameLauncherService {

    private static final int TICK_RATE = 15;
    private static final int TICK_DELAY = 1000 / TICK_RATE;

    private AtomicBoolean running;

    private ApplicationEventPublisher applicationEventPublisher;

    private final GameStateService stateService;
    private final OutputService outputService;

    public GameLauncherServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                   GameStateService gameStateService,
                                   OutputService outputService) {

        this.running = new AtomicBoolean();

        this.applicationEventPublisher = applicationEventPublisher;
        this.stateService = gameStateService;
        this.outputService = outputService;
    }

    @Override
    public void start() {
        running.set(true);
        while (running.get()) {
            try {

                Instant t0 = Instant.now();

                State state = stateService.calculateGameState(TICK_DELAY);
                Map<String, StateSnapshot> snapshots = outputService.toSnapshots(state);

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
