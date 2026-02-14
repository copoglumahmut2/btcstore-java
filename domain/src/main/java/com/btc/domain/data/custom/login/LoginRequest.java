package com.btc.domain.data.custom.login;

import com.btc.domain.data.store.login.StoreLoginRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class LoginRequest extends StoreLoginRequest {
}
