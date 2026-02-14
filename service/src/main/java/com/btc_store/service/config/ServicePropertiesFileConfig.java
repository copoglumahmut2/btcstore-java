package com.btc_store.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:service-${spring.profiles.active}.properties")
public class ServicePropertiesFileConfig {
}