package com.btc_store.service;

import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

public interface CategoryService {

    CategoryModel getCategoryByCode(String code, SiteModel siteModel);

    List<CategoryModel> getAllCategories(SiteModel siteModel);

    List<CategoryModel> getActiveCategories(SiteModel siteModel);

    List<CategoryModel> getAllCategoriesOrdered(SiteModel siteModel);

    List<CategoryModel> getActiveCategoriesOrdered(SiteModel siteModel);

    CategoryModel saveCategory(CategoryModel categoryModel);

    void deleteCategory(CategoryModel categoryModel);
}
