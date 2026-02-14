package com.btc_store.domain.data.store.search;

import com.btc_store.domain.enums.SortDirection;
import lombok.Data;

@Data
public class StoreSearchSortData {

    private String field;
    private SortDirection direction;
}
