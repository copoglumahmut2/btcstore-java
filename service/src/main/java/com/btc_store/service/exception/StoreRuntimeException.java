package com.btc_store.service.exception;

import lombok.Getter;

@Getter
public class StoreRuntimeException extends RuntimeException {

    protected transient Object[] args;

    protected String messageKey;

    public StoreRuntimeException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public StoreRuntimeException(String message) {
        super(message);
    }

    public StoreRuntimeException(Throwable cause) {
        super(cause);
    }

    public StoreRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
