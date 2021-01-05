package com.metalheart;

import com.metalheart.service.GameLauncherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class App {

    @Autowired
    private GameLauncherService launcherService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @EventListener
    private void handleApplicationStartup(ApplicationReadyEvent event) {
        launcherService.start();
    }
}