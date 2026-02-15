package com.btc_store.facade;

import com.btc_store.domain.data.custom.itemtype.ItemTypeData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.data.custom.search.SearchResultData;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface SearchFacade {

    List<ItemTypeData> getAllItemModels();

    PageableData search(String itemType, Pageable pageable, String searchFormData);

    PageableData searchByQuery(String itemType, Pageable pageable, String searchQueryData);

    InputStream exportExcel(String itemType, String searchFormData);

    List<String> getAllItemFields(String code);

    List<String> getAllItemFields(Set<String> items);

    List<String> getAllItemFieldsExceptMany(String code);

    List<SearchResultData> searchAll(String itemType);
}
