package com.btc.service.exception.user;

import com.btc.service.exception.StoreRuntimeException;

public class UserNotActiveException extends StoreRuntimeException {
    public UserNotActiveException(String message, String messageKey, Object[] args) {
        super(message, messageKey, args);
    }

    public UserNotActiveException(String message) {
        super(message);
    }

    public UserNotActiveException(Throwable cause) {
        super(cause);
    }

    public UserNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
