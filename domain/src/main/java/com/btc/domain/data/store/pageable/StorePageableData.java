package com.btc.domain.data.store.pageable;

import lombok.Data;

import java.util.List;

@Data
public class StorePageableData {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<Object> content;
}
