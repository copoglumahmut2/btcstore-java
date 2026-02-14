package com.btc_store.domain.model.custom.extend;

import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import java.io.Serial;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class SiteBasedItemModel extends StoreSiteBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;
}