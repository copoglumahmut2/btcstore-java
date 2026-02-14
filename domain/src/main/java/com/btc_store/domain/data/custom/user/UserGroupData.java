package com.btc_store.domain.data.custom.user;

import com.btc_store.domain.data.store.user.StoreUserGroupData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class UserGroupData extends StoreUserGroupData {
}
