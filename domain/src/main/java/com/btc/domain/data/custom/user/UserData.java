package com.btc.domain.data.custom.user;

import com.btc.domain.data.store.user.StoreUserData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class UserData extends StoreUserData {
}
