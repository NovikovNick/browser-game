package com.metalheart.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ServiceModuleConfiguration.class)
@ComponentScan("com.metalheart.showcase")
public class ShowcaseModuleConfiguration {
}
