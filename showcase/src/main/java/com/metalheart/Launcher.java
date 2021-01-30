package com.metalheart;

import com.metalheart.config.ShowcaseModuleConfiguration;
import com.metalheart.showcase.service.CanvasService;
import com.metalheart.showcase.Showcase;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(ShowcaseModuleConfiguration.class);
        var canvasService = context.getBean(CanvasService.class);
        var showcase = context.getBean(Showcase.class);

        stage.setScene(canvasService.createScene());
        stage.centerOnScreen();
        stage.setFullScreen(true);
        stage.show();

        showcase.start();
    }
}