package com.btc_store.domain.data.store.base.back;

import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class StoreBackBaseData extends BaseData {
    private String createdBy;
    private String lastModifiedBy;
    private Date createdDate;
    private Date lastModifiedDate;
}