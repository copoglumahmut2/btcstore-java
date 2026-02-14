package com.btc_store.persistence.dao;

import com.btc_store.domain.data.custom.search.SearchFormData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.extend.ItemModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SearchDao {


    <T extends ItemModel> T searchSingleResult(Class<T> tableClass, Map parameter, SearchOperator searchOperator);

    <T extends ItemModel> Collection<T> search(Class<T> tableClass, Map parameter, SearchOperator searchOperator);

    <T extends ItemModel> List<T> search(Class<T> tableClass, SearchFormData searchFormData, SiteModel siteModel);

    <T extends ItemModel> T searchSingleResultRelation(T t, String... relation);

    <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, SearchFormData searchFormData, SiteModel siteModel);

    <T extends ItemModel> List<T> search(Class<T> tableClass, String query, Map parameter);

    <T extends ItemModel> Page<T> search(Class<T> tableClass,Pageable pageable, String query, Map parameter);

    Object searchSingleResultField(Class<? extends ItemModel> tableClass, Map parameter, SearchOperator searchOperator, String... fields);

    Map<List<String>, Page<Map<String, Object>>> searchWithNativeSql(Pageable pageable, String querySql) throws SQLException;

    Set<String> getAllTableNames();

}


