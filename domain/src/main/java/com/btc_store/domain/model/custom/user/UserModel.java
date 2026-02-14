package com.btc_store.domain.model.custom.user;


import com.btc_store.domain.model.store.user.StoreUserModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UserModel extends StoreUserModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
