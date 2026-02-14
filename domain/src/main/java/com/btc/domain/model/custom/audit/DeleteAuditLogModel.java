package com.btc.domain.model.custom.audit;

import com.btc.domain.model.store.audit.StoreDeleteAuditLogModel;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
public class DeleteAuditLogModel extends StoreDeleteAuditLogModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
