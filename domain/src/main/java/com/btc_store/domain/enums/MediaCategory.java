package com.btc_store.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MediaCategory {
    BANNER("banner_cms"),
    CATEGORY("category_cms"),
    SUCCESS_STORY("success_story_cms"),
    REFERENCE("reference_cms"),
    PARTNER("partner_cms"),
    USER("user_cms");

    private String value;
}
