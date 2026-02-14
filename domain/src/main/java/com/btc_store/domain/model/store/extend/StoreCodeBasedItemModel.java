package com.btc_store.domain.model.store.extend;

import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import java.io.Serial;

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@FieldNameConstants
public abstract class StoreCodeBasedItemModel extends SiteBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;


    @NotEmpty(message = "{backValidation.codeBasedItemModel.code.notEmpty}")
    @Size(min = 1, message = "{backValidation.codeBasedItemModel.code.min.oneChar}")
    @EqualsAndHashCode.Include
    private String code;
}