package com.metalheart.service.impl;

import com.metalheart.model.Player;
import com.metalheart.model.ServerTicEvent;
import com.metalheart.model.StateSnapshot;
import com.metalheart.service.GameLauncherService;
import com.metalheart.service.GameStateService;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameLauncherServiceImpl implements GameLauncherService {

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

                Set<Player> players = stateService.getGameState().entrySet().stream()
                    .map(entry -> Player.builder()
                        .id(entry.getKey())
                        .x(entry.getValue().getD0())
                        .y(entry.getValue().getD1())
                        .build())
                    .collect(Collectors.toSet());

                StateSnapshot stateSnapshot = StateSnapshot.builder()
                    .sequenceNumber(sequenceNumber.incrementAndGet())
                    .timestamp(Instant.now().toEpochMilli())
                    .players(players)
                    .build();

                applicationEventPublisher.publishEvent(new ServerTicEvent(stateSnapshot));

                TimeUnit.MILLISECONDS.sleep(98 - Instant.now().minusMillis(t0.toEpochMilli()).toEpochMilli());

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
