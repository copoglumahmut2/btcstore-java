package com.btc.service;

import com.btc.domain.data.custom.search.SearchFormData;
import com.btc.domain.enums.SearchOperator;
import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.extend.ItemModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchService {
    <T extends ItemModel> T searchSingleResult(Class<T> tableClass, Map parameter, SearchOperator searchOperator);

    <T extends ItemModel> T searchByCodeAndSite(Class<T> tableClass, String code, SiteModel site);

    Object searchSingleResultField(Class<? extends ItemModel> tableClass, Map parameter, SearchOperator searchOperator, String... field);

    <T extends ItemModel> Collection<T> search(Class<T> tableClass, Map parameter, SearchOperator searchOperator);

    <T extends ItemModel> T searchSingleResultRelation(T t, String... relationName);

    <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, SearchFormData searchFormData, SiteModel siteModel);

    <T extends ItemModel> List<T> search(Class<T> tableClass, String query, Map parameter);

    <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, String query, Map parameter);

    Map<List<String>, Page<Map<String, Object>>> searchWithNativeSql(Pageable pageable, String query);

    Set<String> getAllTableNames();

}
