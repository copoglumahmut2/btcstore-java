package com.btc_store.service.interceptor.registry;

import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.service.interceptor.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Getter
@AllArgsConstructor
public class InterceptorRegistry {


    protected final ApplicationContext context;

    public Map<Interceptor, Class<? extends ItemModel>> beforeSaveInterceptor;
    public Map<Interceptor, Class<? extends ItemModel>> beforeRemoveInterceptor;
    public Map<Interceptor, Class<? extends ItemModel>> afterSaveInterceptor;
    public Map<Interceptor, Class<? extends ItemModel>> afterRemoveInterceptor;
    public Map<Interceptor, Class<? extends ItemModel>> loadInterceptor;

    @Bean(name = "registerAllInterceptors")
    void registerAllInterceptors() {


        var beforeSaveClasses = context.getBeansWithAnnotation(BeforeSaveInterceptor.class);
        beforeSaveClasses.entrySet().forEach(interceptor -> beforeSaveInterceptor.put(getExtendValue(interceptor.getValue()),
                interceptor.getValue().getClass().getAnnotation(BeforeSaveInterceptor.class).itemType()));


        var afterSaveClasses = context.getBeansWithAnnotation(AfterSaveInterceptor.class);
        afterSaveClasses.entrySet().forEach(interceptor -> afterSaveInterceptor.put(getExtendValue(interceptor.getValue()),
                interceptor.getValue().getClass().getAnnotation(AfterSaveInterceptor.class).itemType()));

        var beforeRemoveClasses = context.getBeansWithAnnotation(BeforeRemoveInterceptor.class);
        beforeRemoveClasses.entrySet().forEach(interceptor -> beforeRemoveInterceptor.put(getExtendValue(interceptor.getValue()),
                interceptor.getValue().getClass().getAnnotation(BeforeRemoveInterceptor.class).itemType()));

        var afterRemoveClasses = context.getBeansWithAnnotation(AfterRemoveInterceptor.class);
        afterRemoveClasses.entrySet().forEach(interceptor -> afterRemoveInterceptor.put(getExtendValue(interceptor.getValue()),
                interceptor.getValue().getClass().getAnnotation(AfterRemoveInterceptor.class).itemType()));

        var postLoadClasses = context.getBeansWithAnnotation(PostLoadInterceptor.class);
        postLoadClasses.entrySet().forEach(interceptor -> loadInterceptor.put(getExtendValue(interceptor.getValue()),
                interceptor.getValue().getClass().getAnnotation(PostLoadInterceptor.class).itemType()));

    }

    protected Interceptor getExtendValue(Object bean) {

        if (Objects.nonNull(bean.getClass().getAnnotation(Primary.class))) {
            return (Interceptor) context.getBean(bean.getClass().getSuperclass());
        }
        return (Interceptor) context.getBean(bean.getClass());

    }

}
