package com.btc.domain.data.store.search;

import com.btc.domain.data.custom.search.SearchFilter;
import com.btc.domain.data.custom.search.SearchSortData;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class StoreSearchFormData {

    private Set<SearchFilter> filters;
    private SearchSortData sort;
    /*For excel exports*/
    private int totalCount;
    private LinkedHashSet<String> headers;

}
