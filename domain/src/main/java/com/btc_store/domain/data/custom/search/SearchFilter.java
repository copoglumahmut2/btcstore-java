package com.btc_store.domain.data.custom.search;

import com.btc_store.domain.data.store.search.StoreSearchFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class SearchFilter extends StoreSearchFilter {

}
