package com.btc_store.domain.model.custom.dataintegration;

import com.btc_store.domain.model.store.dataintegration.StoreDataIntegrationLogModel;
import jakarta.persistence.Entity;

import java.io.Serial;

@Entity
public class DataIntegrationLogModel extends StoreDataIntegrationLogModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
