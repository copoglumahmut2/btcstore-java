package com.btc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchOperator {
    AND("AND"), OR("OR");

    private String value;
}
