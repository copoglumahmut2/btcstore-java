package com.btc_store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef="auditServiceImpl")
@EnableCaching
@EnableAsync
@Slf4j
@EnableScheduling
@EnableAspectJAutoProxy
public class BtcStoreApplication {
    public static void main(String[] args) {
        log.info("Started Btc Store Application");
        SpringApplication.run(BtcStoreApplication.class, args);
    }
}
