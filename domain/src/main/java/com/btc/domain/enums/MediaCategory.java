package com.btc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MediaCategory {
    PRODUCT("product"),
    BANNER("banner"),
    OTHER("other");

    private String value;
}
