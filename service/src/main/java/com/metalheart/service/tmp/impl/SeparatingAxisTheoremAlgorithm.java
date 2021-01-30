package com.metalheart.service.tmp.impl;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.service.state.CollisionDetectionService;
import com.metalheart.service.tmp.CollisionPair;
import com.metalheart.service.tmp.NarrowPhaseAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class SeparatingAxisTheoremAlgorithm implements NarrowPhaseAlgorithm {

    private final CollisionDetectionService collisionDetectionService;

    public SeparatingAxisTheoremAlgorithm(CollisionDetectionService collisionDetectionService) {
        this.collisionDetectionService = collisionDetectionService;
    }

    @Override
    public CollisionResult findCollision(CollisionPair pair) {
        return collisionDetectionService.detectCollision(pair.getIncident().getShape(), pair.getReference().getShape());
    }
}
