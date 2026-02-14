package com.btc_store.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImportProcessType {

    SAVE("Save"), REMOVE("Remove"), FILE("File");

    private String value;

}
