package com.btc_store.service.interceptor;

import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.service.exception.interceptor.InterceptorException;

public interface Interceptor<T extends ItemModel> {

    void invoke(T model) throws InterceptorException;

    default boolean isModified(T model, String attribute) {
        return EntityModificationUtil.isModified(model, attribute);
    }

    default boolean isNew(T model) {
        return model.isNewTransaction();
    }
}
