package com.btc.domain.model.store.extend;

import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.extend.ItemModel;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import java.io.Serial;

@Getter
@Setter
@MappedSuperclass
@FieldNameConstants
public abstract class StoreSiteBasedItemModel extends ItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String SITE_RELATION = "site_id";

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "{backValidation.itemModel.site.notEmpty}")
    private SiteModel site;

}
