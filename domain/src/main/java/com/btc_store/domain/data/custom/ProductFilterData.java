package com.btc_store.domain.data.custom;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductFilterData {
    private List<ProductData> products = new ArrayList<>();
    private List<CategoryData> availableCategories = new ArrayList<>();
    private CategoryData selectedCategory;
    private Long totalProducts;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPages;
}
