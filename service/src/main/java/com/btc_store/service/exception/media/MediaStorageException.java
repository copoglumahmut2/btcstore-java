package com.btc_store.service.exception.media;

import com.btc_store.service.exception.StoreRuntimeException;

public class MediaStorageException extends StoreRuntimeException {

    public MediaStorageException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public MediaStorageException(String message) {
        super(message);
    }

    public MediaStorageException(Throwable cause) {
        super(cause);
    }

    public MediaStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
