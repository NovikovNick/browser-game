package com.metalheart.service.input.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.struct.LimitedList;
import com.metalheart.service.input.PlayerInputService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

@Service
public class PlayerInputServiceImpl implements PlayerInputService {

    private static final int MAX_INPUT = 10;

    private final Map<String, LimitedList<PlayerInput>> inputs;
    private final Lock lock;

    public PlayerInputServiceImpl() {
        this.inputs = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public void add(String playerId, PlayerInput input) {
        lock.lock();
        try {
            inputs.putIfAbsent(playerId, new LimitedList<>(MAX_INPUT));
            inputs.get(playerId).add(input);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<String, List<PlayerInput>> pop() {

        HashMap<String, List<PlayerInput>> res = new HashMap<>();
        lock.lock();
        try{
            inputs.forEach((sessionId, snapshots) -> res.put(sessionId, snapshots.pollAll()));
            inputs.clear();
        } finally {
            lock.unlock();
        }
        return res;
    }
}
