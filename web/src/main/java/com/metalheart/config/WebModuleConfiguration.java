package com.metalheart.config;

import com.metalheart.socket.WebSocketConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    SwaggerConfiguration.class,
    WebSocketConfiguration.class
})
@ComponentScan({
    "com.metalheart.controller",
    "com.metalheart.socket"
})
public class WebModuleConfiguration {

}
