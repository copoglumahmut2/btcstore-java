package com.btc_store.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MediaCategory {
    BANNER("banner_cms"),
    CATEGORY("category_cms");

    private String value;
}
