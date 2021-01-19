package com.metalheart.controller;

import com.metalheart.model.ChangePlayerNameRequest;
import com.metalheart.model.ChangePlayerNameResponse;
import com.metalheart.service.state.GameStateService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class Controller {

    private GameStateService gameStateService;

    public Controller(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    @ApiOperation(value = "Change username")
    @PostMapping(path = "/player",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChangePlayerNameResponse> createTask(@RequestBody ChangePlayerNameRequest req) {

        String username = gameStateService.updateUsername(req.getSessionId(), req.getUsername());
        return ResponseEntity.ok(new ChangePlayerNameResponse(username));
    }
}
