package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.CategoryDao;
import com.btc_store.service.CategoryService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    @Override
    public CategoryModel getCategoryByCode(String code, SiteModel siteModel) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(siteModel, "Site must not be null");
        var categoryModel = categoryDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(categoryModel, CategoryModel.class, siteModel, code);
        return categoryModel;
    }

    @Override
    public List<CategoryModel> getAllCategories(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return categoryDao.getAllBySite(siteModel);
    }

    @Override
    public List<CategoryModel> getActiveCategories(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return categoryDao.getAllBySiteAndActiveTrue(siteModel);
    }

    @Override
    public List<CategoryModel> getAllCategoriesOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return categoryDao.getAllBySiteOrderByOrderAsc(siteModel);
    }

    @Override
    public List<CategoryModel> getActiveCategoriesOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return categoryDao.getAllBySiteAndActiveTrueOrderByOrderAsc(siteModel);
    }

    @Override
    public CategoryModel saveCategory(CategoryModel categoryModel) {
        Assert.notNull(categoryModel, "Category model must not be null");
        return categoryDao.save(categoryModel);
    }

    @Override
    public void deleteCategory(CategoryModel categoryModel) {
        Assert.notNull(categoryModel, "Category model must not be null");
        categoryDao.delete(categoryModel);
    }
}
