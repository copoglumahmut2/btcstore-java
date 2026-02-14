package com.btc.domain.model.custom.user;


import com.btc.domain.model.store.user.StoreUserGroupModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserGroupModel extends StoreUserGroupModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
