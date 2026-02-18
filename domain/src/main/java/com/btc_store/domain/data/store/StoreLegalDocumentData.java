package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.LegalDocumentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreLegalDocumentData extends BackBaseData {

    private LegalDocumentType documentType;
    private LocalizeData title;
    private LocalizeData content;
    private String version;
    private Date effectiveDate;
    private Boolean isCurrentVersion;
    private LocalizeData shortText;
    private Boolean active;
}
