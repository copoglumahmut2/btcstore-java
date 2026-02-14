package com.btc_store.service.exception.model;

import com.btc_store.service.exception.StoreRuntimeException;

public class ModelRemoveException extends StoreRuntimeException {

    public ModelRemoveException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public ModelRemoveException(String message) {
        super(message);
    }

    public ModelRemoveException(Throwable cause) {
        super(cause);
    }

    public ModelRemoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
