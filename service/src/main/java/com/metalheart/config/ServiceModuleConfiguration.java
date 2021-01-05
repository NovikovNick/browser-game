package com.metalheart.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
    "com.metalheart.service"
})
public class ServiceModuleConfiguration {
}
