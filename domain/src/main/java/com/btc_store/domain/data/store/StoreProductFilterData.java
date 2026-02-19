package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.data.custom.ProductData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StoreProductFilterData {
    private List<ProductData> products = new ArrayList<>();
    private List<CategoryData> availableCategories = new ArrayList<>();
    private CategoryData selectedCategory;
    private Long totalProducts;
}
