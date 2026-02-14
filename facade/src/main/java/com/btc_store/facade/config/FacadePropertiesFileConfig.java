package com.btc_store.facade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:facade-${spring.profiles.active}.properties")
public class FacadePropertiesFileConfig {
}
