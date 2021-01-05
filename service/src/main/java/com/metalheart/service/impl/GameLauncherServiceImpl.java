package com.metalheart.service.impl;

import com.metalheart.model.ServerTicEvent;
import com.metalheart.service.GameLauncherService;
import com.metalheart.service.GameStateService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameLauncherServiceImpl implements GameLauncherService {

    private AtomicBoolean running;

    private ApplicationEventPublisher applicationEventPublisher;

    private GameStateService stateService;

    public GameLauncherServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                   GameStateService gameStateService) {
        this.running = new AtomicBoolean();
        this.applicationEventPublisher = applicationEventPublisher;
        this.stateService = gameStateService;
    }

    @Override
    public void start() {
        running.set(true);
        while (running.get()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                applicationEventPublisher.publishEvent(new ServerTicEvent(stateService.getGameState()));
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
