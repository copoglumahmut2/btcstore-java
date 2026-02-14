package com.btc.service.exception.media;

import com.btc.service.exception.StoreRuntimeException;

public class MediaDeleteException extends StoreRuntimeException {

    public MediaDeleteException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public MediaDeleteException(String message) {
        super(message);
    }

    public MediaDeleteException(Throwable cause) {
        super(cause);
    }

    public MediaDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
