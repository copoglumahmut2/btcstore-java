package com.btc_store.service.interceptor.registry;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class HibernateConfig {

    @PersistenceUnit
    protected EntityManagerFactory emf;

    protected final CallBackPostLoadEventListener callBackPostLoadEventListener;

    /*
        Post Load Interceptor olarak belirlenen anotasyonları çalıştırmak için oluşturulan
        event listener hibernate e aşağıdaki metot ile tanıtılır.
     */

    @Bean
    public void registerListeners() {
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory
                .getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_LOAD).appendListeners(callBackPostLoadEventListener);

    }
}
