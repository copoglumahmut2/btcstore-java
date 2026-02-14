package com.btc_store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:webapp-${spring.profiles.active}.properties"),
        @PropertySource("classpath:log-${spring.profiles.active}.properties")
})
public class WebappPropertiesFileConfig {

}
