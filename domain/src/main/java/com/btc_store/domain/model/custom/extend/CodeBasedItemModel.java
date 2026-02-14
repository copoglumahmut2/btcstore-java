package com.btc_store.domain.model.custom.extend;

import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import java.io.Serial;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class CodeBasedItemModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;
}