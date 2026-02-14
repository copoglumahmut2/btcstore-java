package com.btc.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:persistence-${spring.profiles.active}.properties")
public class PersistencePropertiesFileConfig {
}
