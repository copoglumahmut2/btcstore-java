package com.btc.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchCondition {
    EQUALS,NOT_EQUALS, LIKE, STARTS_WITH, ISEMPTY,
    ISNOTEMPTY, LESS, GREATER, LESSOREQUAL,
    GREATEROREQUAL, CONTAINS, NOTCONTAINS,IN, NOTNULL, BETWEEN
}
