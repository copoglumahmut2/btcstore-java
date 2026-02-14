package com.btc.service.exception.user;

import org.springframework.security.core.AuthenticationException;

public class UserLockedException extends AuthenticationException {

    public UserLockedException(String message) {
        super(message);
    }

    public UserLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
