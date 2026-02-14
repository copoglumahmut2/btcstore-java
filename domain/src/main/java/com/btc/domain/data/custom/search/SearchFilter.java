package com.btc.domain.data.custom.search;

import com.btc.domain.data.store.search.StoreSearchFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class SearchFilter extends StoreSearchFilter {

}
