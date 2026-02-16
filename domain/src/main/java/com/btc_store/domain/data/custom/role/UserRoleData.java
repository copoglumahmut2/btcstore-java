package com.btc_store.domain.data.custom.role;

import com.btc_store.domain.data.store.role.StoreUserRoleData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class UserRoleData extends StoreUserRoleData {
}
