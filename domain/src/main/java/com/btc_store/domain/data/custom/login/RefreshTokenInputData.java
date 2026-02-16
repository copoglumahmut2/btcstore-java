package com.btc_store.domain.data.custom.login;

import com.btc_store.domain.data.store.login.StoreRefreshTokenInputData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class RefreshTokenInputData extends StoreRefreshTokenInputData {
}
