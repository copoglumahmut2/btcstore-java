package com.btc_store.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:security-${spring.profiles.active}.properties")
public class SecurityPropertiesFileConfig {

}
