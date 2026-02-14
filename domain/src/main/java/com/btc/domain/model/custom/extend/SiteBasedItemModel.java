package com.btc.domain.model.custom.extend;

import com.btc.domain.model.store.extend.StoreSiteBasedItemModel;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import java.io.Serial;

@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class SiteBasedItemModel extends StoreSiteBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;
}