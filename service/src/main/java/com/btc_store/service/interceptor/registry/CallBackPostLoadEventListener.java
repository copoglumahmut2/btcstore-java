package com.btc_store.service.interceptor.registry;

import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.springframework.stereotype.Component;
import util.StoreClassUtils;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class CallBackPostLoadEventListener implements PostLoadEventListener {

    protected final InterceptorRegistry interceptorRegistry;

    /*
            Burada tek bir event listener içerisinde gelen eventlere göre
            PostLoadInterceptor olarak belirlenen anotasyonlara gidilir.Model Servis te kullanılan mantık
            ile aynı.
     */

    @Override
    public void onPostLoad(PostLoadEvent event) {
        var entity = event.getEntity();

        var interceptor = StringUtils.EMPTY;
        try {
            if (entity instanceof ItemModel) {

                var loadInterceptors = interceptorRegistry.getLoadInterceptor().entrySet().stream()
                        .filter(p -> StringUtils.equals(p.getValue().getTypeName(), entity.getClass().getTypeName()))
                        .map(Map.Entry::getKey).toList();


                for (Interceptor<ItemModel> p : loadInterceptors) {
                    interceptor = StoreClassUtils.getSimpleName(p);
                    p.invoke((ItemModel) entity);
                }
            }
        } catch (Exception e) {
            if (e instanceof InterceptorException) {
                throw new InterceptorException("Interceptor : " + interceptor
                        + StringUtils.SPACE + ExceptionUtils.getMessage(e), ((InterceptorException) e).getMessageKey(), ((InterceptorException) e).getArgs());
            } else {
                throw new InterceptorException("Interceptor : " + interceptor
                        + StringUtils.SPACE + ExceptionUtils.getMessage(e));
            }
        }

    }
}
