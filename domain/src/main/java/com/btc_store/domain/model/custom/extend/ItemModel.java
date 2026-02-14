package com.btc_store.domain.model.custom.extend;

import com.btc_store.domain.model.store.extend.StoreItemModel;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import java.io.Serial;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class ItemModel extends StoreItemModel {

    @Serial
    private static final long serialVersionUID = 1L;
}