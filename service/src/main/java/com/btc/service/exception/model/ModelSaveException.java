package com.btc.service.exception.model;

import com.btc.service.exception.StoreRuntimeException;

public class ModelSaveException extends StoreRuntimeException {

    public ModelSaveException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public ModelSaveException(String message) {
        super(message);
    }

    public ModelSaveException(Throwable cause) {
        super(cause);
    }

    public ModelSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
