package com.btc_store.service.exception.model;

import com.btc_store.service.exception.StoreRuntimeException;

public class ModelNotFoundException extends StoreRuntimeException {

    public ModelNotFoundException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(Throwable cause) {
        super(cause);
    }

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
