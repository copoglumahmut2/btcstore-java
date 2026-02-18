package com.btc_store.domain.data.store;

import com.btc_store.domain.data.extend.back.BackBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreEmailTemplateData extends BackBaseData {
    
    private String templateName;
    private String relatedItem;
    private String subject;
    private String body;
    private String description;
    private Boolean active;
}
