package com.btc.domain.data.store.search;

import com.btc.domain.enums.SortDirection;
import lombok.Data;

@Data
public class StoreSearchSortData {

    private String field;
    private SortDirection direction;
}
