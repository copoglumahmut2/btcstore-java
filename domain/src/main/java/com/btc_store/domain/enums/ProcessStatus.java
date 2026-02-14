package com.btc_store.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProcessStatus {
    SUCCESS("SUCCESS"), ERROR("ERROR"),UNAUTHORIZED("UNAUTHORIZED");

    private String value;
}
