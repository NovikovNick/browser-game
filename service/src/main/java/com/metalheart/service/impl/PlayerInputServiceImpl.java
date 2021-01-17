package com.metalheart.service.impl;

import com.metalheart.model.PlayerInput;
import com.metalheart.service.PlayerInputService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

@Service
public class PlayerInputServiceImpl implements PlayerInputService {

    private static final int MAX_INPUT = 10;

    private final Map<String, TreeSet<PlayerInput>> inputs;
    private final Lock lock;

    public PlayerInputServiceImpl() {
        this.inputs = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public void add(String playerId, PlayerInput input) {
        lock.lock();
        try {
            inputs.putIfAbsent(playerId, new TreeSet<>(Comparator.comparing(PlayerInput::getTime)));
            TreeSet<PlayerInput> playerInputs = inputs.get(playerId);

            if (playerInputs.size() > MAX_INPUT) {
                playerInputs.pollFirst();
            }

            playerInputs.add(input);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<String, Set<PlayerInput>> pop() {

        HashMap<String, Set<PlayerInput>> res;
        lock.lock();
        try{
            res = new HashMap<>(inputs);
            inputs.clear();
        } finally {
            lock.unlock();
        }
        return res;
    }
}
