package com.btc.domain.model.custom;

import com.btc.domain.model.store.StoreUserAuditModel;
import jakarta.persistence.Entity;

import java.io.Serial;

@Entity
public class UserAuditModel extends StoreUserAuditModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
