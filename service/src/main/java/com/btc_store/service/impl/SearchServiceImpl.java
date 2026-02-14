package com.btc_store.service.impl;

import com.btc_store.domain.data.custom.search.SearchFormData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.persistence.dao.SearchDao;
import com.btc_store.service.SearchService;
import com.btc_store.service.exception.StoreRuntimeException;
import com.btc_store.service.exception.model.ModelNotFoundException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    protected final SearchDao searchDao;

    @Override
    public <T extends ItemModel> T searchSingleResult(Class<T> tableClass, Map parameter, SearchOperator searchOperator) {
        try {
            return searchDao.searchSingleResult(tableClass, parameter, searchOperator);
        } catch (NoResultException | EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(e.getMessage());
        }

    }

    @Override
    public <T extends ItemModel> T searchByCodeAndSite(Class<T> tableClass, String code, SiteModel site) {
        try {
            Assert.notNull(code, "Code must not be null");
            var itemSearchMap = new HashMap<>();
            itemSearchMap.put(CodeBasedItemModel.Fields.code, code);
            itemSearchMap.put(SiteBasedItemModel.Fields.site, site);
            return searchDao.searchSingleResult(tableClass, itemSearchMap, SearchOperator.AND);
        } catch (NoResultException | EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(e.getMessage());
        }

    }

    @Override
    public Object searchSingleResultField(Class<? extends ItemModel> tableClass, Map parameter, SearchOperator searchOperator, String... fields) {
        try {
            return searchDao.searchSingleResultField(tableClass, parameter, searchOperator, fields);
        } catch (NoResultException | EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(e.getMessage());
        }
    }

    @Override
    public <T extends ItemModel> Collection<T> search(Class<T> tableClass, Map parameter, SearchOperator searchOperator) {
        return searchDao.search(tableClass, parameter, searchOperator);
    }

    @Override
    public <T extends ItemModel> T searchSingleResultRelation(T t, String... relation) {
        return searchDao.searchSingleResultRelation(t, relation);
    }

    @Override
    public <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, SearchFormData searchFormData, SiteModel siteModel) {
        return searchDao.search(tableClass, pageable, searchFormData, siteModel);
    }

    @Override
    public <T extends ItemModel> List<T> search(Class<T> tableClass, String query, Map parameter) {
        return searchDao.search(tableClass, query, parameter);
    }

    @Override
    public <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, String query, Map parameter) {
        return searchDao.search(tableClass, pageable, query, parameter);
    }

    @Override
    public Map<List<String>, Page<Map<String, Object>>> searchWithNativeSql(Pageable pageable, String query) {

        try {
            return searchDao.searchWithNativeSql(pageable, query);
        } catch (Exception e){
            throw new StoreRuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<String> getAllTableNames() {
        try {
            return searchDao.getAllTableNames();
        } catch (Exception e){
            throw new StoreRuntimeException(e.getMessage());
        }
    }
}
