package com.btc.service.exception.model;

import com.btc.service.exception.StoreRuntimeException;

public class ModelReadException extends StoreRuntimeException {

    public ModelReadException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public ModelReadException(String message) {
        super(message);
    }

    public ModelReadException(Throwable cause) {
        super(cause);
    }

    public ModelReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
