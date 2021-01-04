package com.metalheart;

import com.metalheart.config.WebModuleConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    WebModuleConfiguration.class
})
public class AppConfiguration {
}
