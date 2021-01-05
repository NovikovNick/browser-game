package com.metalheart;

import com.metalheart.config.ServiceModuleConfiguration;
import com.metalheart.config.WebModuleConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    WebModuleConfiguration.class,
    ServiceModuleConfiguration.class
})
public class AppConfiguration {
}
