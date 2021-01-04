package com.metalheart.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

    @ApiOperation(value = "Test operation")
    @GetMapping(path = "/test")
    public ResponseEntity<String> createTask() {
        return ResponseEntity.ok("it works...");
    }
}
