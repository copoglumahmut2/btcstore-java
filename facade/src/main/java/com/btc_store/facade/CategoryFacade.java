package com.btc_store.facade;

import com.btc_store.domain.data.custom.CategoryData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryFacade {

    List<CategoryData> getAllCategories();

    List<CategoryData> getActiveCategories();

    CategoryData getCategoryByCode(String code);

    CategoryData saveCategory(CategoryData categoryData, MultipartFile mediaFile, boolean removeMedia);

    void deleteCategory(String code);
}
