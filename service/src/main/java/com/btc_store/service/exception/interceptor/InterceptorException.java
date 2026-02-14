package com.btc_store.service.exception.interceptor;


import com.btc_store.service.exception.StoreRuntimeException;

public class InterceptorException extends StoreRuntimeException {
    public InterceptorException(String message, String messageKey, Object[] args) {
        super(message, messageKey, args);
    }

    public InterceptorException(String message) {
        super(message);
    }

    public InterceptorException(Throwable cause) {
        super(cause);
    }

    public InterceptorException(String message, Throwable cause) {
        super(message, cause);
    }
}
