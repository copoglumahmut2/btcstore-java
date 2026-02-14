package com.btc_store.domain.model.custom;

import com.btc_store.domain.model.store.StoreUserAuditModel;
import jakarta.persistence.Entity;

import java.io.Serial;

@Entity
public class UserAuditModel extends StoreUserAuditModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
