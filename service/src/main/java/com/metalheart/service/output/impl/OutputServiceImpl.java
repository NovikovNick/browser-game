package com.metalheart.service.output.impl;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerStatePresentation;
import com.metalheart.model.State;
import com.metalheart.service.output.OutputService;
import com.metalheart.service.output.PlayerSnapshotDeltaService;
import com.metalheart.service.output.PlayerSnapshotReduceService;
import com.metalheart.service.output.PlayerSnapshotService;
import com.metalheart.service.output.PlayerPresentationService;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class OutputServiceImpl implements OutputService {

    private final AtomicLong sequenceNumber;

    private final PlayerSnapshotService projectionService;
    private final PlayerSnapshotDeltaService deltaService;
    private final PlayerPresentationService presentationService;
    private final PlayerSnapshotReduceService reduceService;

    public OutputServiceImpl(PlayerSnapshotService playerSnapshotService,
                             PlayerSnapshotDeltaService deltaService,
                             PlayerPresentationService playerPresentationService,
                             PlayerSnapshotReduceService reduceService) {
        this.sequenceNumber = new AtomicLong();
        this.projectionService = playerSnapshotService;
        this.deltaService = deltaService;
        this.presentationService = playerPresentationService;
        this.reduceService = reduceService;
    }

    @Override
    public Map<String, PlayerSnapshot> toSnapshots(State state) {

        long timestamp = Instant.now().toEpochMilli();
        long sequenceNumber = this.sequenceNumber.incrementAndGet();
        Map<String, PlayerSnapshot> res = new HashMap<>();

        // разделить состояние сервера на проекции для игроков
        // каждый игрок получает только ту доступную ему информацию
        projectionService.splitState(state).forEach((playerId, projection) -> {

            // получить текущее представление игрока
            PlayerStatePresentation presentation = presentationService.getPlayerStatePresentation(playerId);

            // сравнить разницу
            PlayerSnapshot snapshot = deltaService.getDelta(presentation, projection);

            // уменьшить размер дельты
            snapshot = reduceService.reduce(snapshot);

            // проставить порядковый номер
            snapshot.setSequenceNumber(sequenceNumber);
            snapshot.setTimestamp(timestamp);

            // сохранить разницу, чтобы в при получении подтверждения применить ее к текущему представлению игрока
            presentationService.saveSnapshot(playerId, snapshot);

            res.put(playerId, snapshot);
        });

        return res;
    }
}
