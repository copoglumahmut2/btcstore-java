package com.btc_store.domain.model.custom;

import com.btc_store.domain.model.store.StoreDocumentModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class DocumentModel extends StoreDocumentModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
