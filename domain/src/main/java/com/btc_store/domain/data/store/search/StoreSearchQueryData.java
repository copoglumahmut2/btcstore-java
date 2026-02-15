package com.btc_store.domain.data.store.search;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class StoreSearchQueryData {

    private String query;
    /*For excel exports*/
    private int totalCount;
    private LinkedHashSet<String> headers;
}
