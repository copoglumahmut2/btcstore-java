package com.btc.service.exception.system;

import com.btc.service.exception.StoreRuntimeException;
import lombok.Getter;

@Getter
public class StoreSystemException extends StoreRuntimeException {

    protected transient Object[] args;

    protected String messageKey;

    public StoreSystemException(String message, String messageKey, Object[] args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args;
    }

    public StoreSystemException(String message) {
        super(message);
    }

    public StoreSystemException(Throwable cause) {
        super(cause);
    }

    public StoreSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
