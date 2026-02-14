package com.btc.domain.model.custom.role;

import com.btc.domain.model.store.role.StoreUserRoleModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserRoleModel extends StoreUserRoleModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
