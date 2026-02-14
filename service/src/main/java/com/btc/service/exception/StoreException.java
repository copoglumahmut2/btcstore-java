package com.btc.service.exception;

import lombok.Getter;

@Getter
public class StoreException extends Exception {

    protected Object[] args;

    protected String messageKey;

    public StoreException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public StoreException(String message) {
        super(message);
    }

    public StoreException(Throwable cause) {
        super(cause);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
