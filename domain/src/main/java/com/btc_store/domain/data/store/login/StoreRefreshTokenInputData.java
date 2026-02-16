package com.btc_store.domain.data.store.login;

import lombok.Data;

import java.io.Serializable;

@Data
public class StoreRefreshTokenInputData implements Serializable {

    private String refreshToken;
}
