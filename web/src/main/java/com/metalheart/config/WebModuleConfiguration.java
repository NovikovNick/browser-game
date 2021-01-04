package com.metalheart.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SwaggerConfiguration.class)
@ComponentScan( {
    "com.metalheart.controller"
})
public class WebModuleConfiguration {

}
