package com.btc_store.service.exception.media;

import com.btc_store.service.exception.StoreRuntimeException;

public class MediaCreateException extends StoreRuntimeException {

    public MediaCreateException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public MediaCreateException(String message) {
        super(message);
    }

    public MediaCreateException(Throwable cause) {
        super(cause);
    }

    public MediaCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
