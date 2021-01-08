package com.metalheart.config;

import com.metalheart.socket.WebSocketConfiguration;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

@Configuration
@Import({
    SwaggerConfiguration.class,
    WebSocketConfiguration.class
})
@ComponentScan({
    "com.metalheart.controller",
    "com.metalheart.converter",
    "com.metalheart.socket"
})
public class WebModuleConfiguration {

    public static final String WEB_CONVERSION_SERVICE = "WEB_CONVERSION_SERVICE";

    @Bean
    @Qualifier(WEB_CONVERSION_SERVICE)
    public ConversionService conversionService(Set<Converter> converters) {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(converters);
        bean.afterPropertiesSet();
        ConversionService object = bean.getObject();
        return object;
    }
}