package com.btc_store.facade.impl;


import com.btc_store.domain.data.custom.itemtype.ItemTypeData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.data.custom.search.SearchFormData;
import com.btc_store.domain.data.custom.search.SearchQueryData;
import com.btc_store.domain.data.custom.search.SearchResultData;
import com.btc_store.domain.data.store.itemtype.StoreItemTypeData;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.SearchFacade;
import com.btc_store.facade.pageable.PageableProvider;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import com.btc_store.service.constant.ServiceConstant;
import com.btc_store.service.encryption.EncryptionService;
import com.btc_store.service.excel.ExcelService;
import com.btc_store.service.exception.StoreRuntimeException;
import com.btc_store.service.exception.model.ModelReadException;
import com.btc_store.service.user.UserGroupService;
import com.btc_store.service.user.UserService;
import constant.PackageConstant;
import jakarta.persistence.Transient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import util.JsonUtil;
import util.StoreClassUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SearchFacadeImpl implements SearchFacade {

    protected final SearchService searchService;
    protected final SiteService siteService;
    protected final UserService userService;
    protected final ModelMapper modelMapper;
    protected final UserGroupService userGroupService;
    protected final PageableProvider pageableProvider;
    protected final EncryptionService encryptionService;
    protected final ExcelService excelService;
    protected final Environment environment;
    public static final String SEARCH_SERVICE_ENCRYPTION_ENABLE = "search.service.encryption.enable";
    protected final ParameterService parameterService;

    @Override
    public PageableData search(String itemType, Pageable pageable, String searchFormData) {
        var itemModels = searchItems(itemType, pageable, searchFormData);
        return pageableProvider.map(itemModels, SearchResultData.class);
    }

    @SneakyThrows
    protected <T extends ItemModel> Page<T> searchItems(String itemType, Pageable pageable, String searchFormData) {
        var siteModel = siteService.getCurrentSite();

        var searchFormDataString = checkEncryptionForData(searchFormData, siteModel);
        SearchFormData decryptedSearchFormData = JsonUtil.convertJsonToObject(searchFormDataString, SearchFormData.class);

        Class<? extends ItemModel> itemClass = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(itemType,
                        ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX),
                PackageConstant.DOMAIN_PACKAGE);

        //check permission...
        checkPermission(itemClass);


        // current user'dan glen veri varssa bunlar dataya çekilerek doldurulur.
        if(Objects.nonNull(decryptedSearchFormData) && CollectionUtils.isNotEmpty(decryptedSearchFormData.getFilters())) {
            for (var searchFilter : decryptedSearchFormData.getFilters()) {
                var currentUser = userService.getCurrentUser();

                if (searchFilter.isCurrentUser()) {

                    var relationField = PropertyUtils.getProperty(currentUser, searchFilter.getCurrentUserRelationField());
                    Object values;
                    if (relationField instanceof Set<?> || relationField instanceof List<?>) {
                        values = ((Collection<?>) relationField).stream().map(p -> {
                            try {
                                return PropertyUtils.getProperty(p, searchFilter.getRelationField());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();

                    } else {
                        values = PropertyUtils.getProperty(currentUser, searchFilter.getCurrentUserRelationField());
                    }
                    PropertyUtils.setProperty(searchFilter, searchFilter.getSetValueForCurrentUser(), values);

                }
            }
        }

        return (Page<T>) searchService.search(itemClass, pageable, decryptedSearchFormData, siteModel);
    }


    @Override
    public InputStream exportExcel(String itemType, String searchFormData) {
        var siteModel = siteService.getCurrentSite();

        var searchFormDataString = checkEncryptionForData(searchFormData, siteModel);
        SearchFormData decryptedSearchFormData = JsonUtil.convertJsonToObject(searchFormDataString, SearchFormData.class);

        //eğer limitten büyük veri varsa export işlemi yapılmaz.Performans için bu engellendi.
        var excelLimit = environment.getProperty("excel.export.max.limit", Integer.class, 10000);
        if (decryptedSearchFormData.getTotalCount() > excelLimit) {
            throw new StoreRuntimeException("Data count is too big for excel export.", "excel.export.limit.message",
                    new Object[]{excelLimit, decryptedSearchFormData.getTotalCount()});
        }

        var itemModels = searchItems(itemType, PageRequest.ofSize(decryptedSearchFormData.getTotalCount()), searchFormData);
        return excelService.exportExcel(itemType, itemModels.getContent(), decryptedSearchFormData.getHeaders());

    }

    private void checkPermission(Class<? extends ItemModel> itemClass) {
        var authorities = userService.getCurrentUserAuthorities();
        authorities.stream().filter(a -> StringUtils.equals(a, ServiceConstant.SUPER_ADMIN) ||
                        StringUtils.equals(a, itemClass.getSimpleName().concat(ServiceConstant.UNDERSCORE)
                                .concat("Read"))).findFirst()
                .orElseThrow(() -> new ModelReadException("You are not authorized to read data from [+" + StoreClassUtils.getSimpleName(itemClass) + "] table",
                        "search.service.not.authorized.read.table", new Object[]{StoreClassUtils.getSimpleName(itemClass)}));
    }

    @Override
    @Cacheable(value = "all-item-models")
    public List<ItemTypeData> getAllItemModels() {
        var itemClasses = StoreClassUtils.getClassesForPackage(PackageConstant.DOMAIN_PACKAGE);

        return itemClasses.stream()
                .filter(c -> StringUtils.startsWith(c.getName(), "com.btc_store.domain.model.custom") &&
                        StringUtils.endsWith(c.getName(), PackageConstant.MODEL_PREFIX))
                .map(p -> {
                    var itemTypeData = new ItemTypeData();
                    itemTypeData.setCode(p.getSimpleName());
                    itemTypeData.setName(p.getSimpleName());
                    return itemTypeData;
                }).sorted(Comparator.comparing(StoreItemTypeData::getName)).toList();

    }

    @Override
    @Cacheable(value = "all-item-fields")
    public List<String> getAllItemFields(String code) {
        var classForPackage = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(code,
                        ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX),
                PackageConstant.DOMAIN_PACKAGE);

        var classFields = new ArrayList<>(Arrays.asList(classForPackage.getDeclaredFields()));
        var superClass = classForPackage.getSuperclass();
        while (Objects.nonNull(superClass)) {
            var superClassFields = new ArrayList<>(Arrays.asList(superClass.getDeclaredFields()));
            classFields.addAll(superClassFields);
            superClass = superClass.getSuperclass();
        }

        return classFields
                .stream()
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .filter(f -> Objects.isNull(f.getAnnotation(Transient.class)))
                .map(Field::getName)
                .filter(p -> !StringUtils.equals(p, "serialVersionUID"))
                .sorted()
                .toList();


    }

    @Override
    public List<String> getAllItemFields(Set<String> items) {
        var fields = new ArrayList<String>();
        items.forEach(item -> fields.addAll(getAllFieldsForOneItem(item)));
        return fields;
    }

    @Override
    public List<String> getAllItemFieldsExceptMany(String code) {
        var classForPackage = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(code,
                        ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX),
                PackageConstant.DOMAIN_PACKAGE);

        var classFields = new ArrayList<>(Arrays.asList(classForPackage.getDeclaredFields()));
        var superClass = classForPackage.getSuperclass();
        while (Objects.nonNull(superClass)) {
            var superClassFields = new ArrayList<>(Arrays.asList(superClass.getDeclaredFields()));
            classFields.addAll(superClassFields);
            superClass = superClass.getSuperclass();
        }

        return classFields
                .stream()
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .filter(f -> Objects.isNull(f.getAnnotation(Transient.class)))
                .filter(f -> !StringUtils.equals(f.getType().getSimpleName(), Set.class.getSimpleName()))
                .filter(f -> !StringUtils.equals(f.getType().getSimpleName(), List.class.getSimpleName()))
                .filter(f -> !StringUtils.equals(f.getType().getSimpleName(), Collection.class.getSimpleName()))
                .map(Field::getName)
                .filter(p -> !StringUtils.equals(p, "serialVersionUID"))
                .sorted()
                .toList();
    }

    private List<String> getAllFieldsForOneItem(String itemCode) {
        var classForPackage = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(itemCode,
                        ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX),
                PackageConstant.DOMAIN_PACKAGE);

        var classFields = new ArrayList<>(Arrays.asList(classForPackage.getDeclaredFields()));
        var superClass = classForPackage.getSuperclass();
        while (Objects.nonNull(superClass)) {
            var superClassFields = new ArrayList<>(Arrays.asList(superClass.getDeclaredFields()));
            classFields.addAll(superClassFields);
            superClass = superClass.getSuperclass();
        }

        return classFields
                .stream()
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .filter(f -> Objects.isNull(f.getAnnotation(Transient.class)))
                .map(Field::getName)
                .filter(f -> !StringUtils.equals(f, "serialVersionUID"))
                .map(f -> String.join(ServiceConstant.DOT, itemCode, f))
                .sorted()
                .toList();
    }

    @Override
    public PageableData searchByQuery(String itemType, Pageable pageable, String searchQueryData) {
        try {
            var siteModel = siteService.getCurrentSite();

            var searchQueryDataString = checkEncryptionForData(searchQueryData, siteModel);
            SearchQueryData decryptedSearchQueryData = JsonUtil.convertJsonToObject(searchQueryDataString, SearchQueryData.class);

            checkSqlInjection(decryptedSearchQueryData.getQuery());

            Class<? extends ItemModel> itemClass = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(itemType,
                            ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX),
                    PackageConstant.DOMAIN_PACKAGE);

            //check permission...
            checkPermission(itemClass);

            var map = Map.of(StoreSiteBasedItemModel.Fields.site, siteModel);

            var itemModels = searchService.search(itemClass, pageable, decryptedSearchQueryData.getQuery(), map);
            return pageableProvider.map(itemModels, SearchResultData.class);
        } catch (Exception e) {
            throw new StoreRuntimeException("Oluşturulan sorgu hatalıdır.");
        }
    }

    private String checkEncryptionForData(String data, SiteModel siteModel) {

        var encryptionEnabledParam = parameterService.getParameterModel(SEARCH_SERVICE_ENCRYPTION_ENABLE, siteModel);
        var isEncryptionEnabled = BooleanUtils.toBoolean(encryptionEnabledParam.getValue());

        return isEncryptionEnabled ? encryptionService.decrypt(data)
                : data;
    }

    @Override
    public List<SearchResultData> searchAll(String itemType) {
        var siteModel = siteService.getCurrentSite();
        var exportLimit = environment.getProperty("search.all.max.size", Integer.class, 10000);
        Class<? extends ItemModel> itemClass = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(itemType,
                        ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX),
                PackageConstant.DOMAIN_PACKAGE);
        SearchFormData searchFormData = new SearchFormData();
        searchFormData.setFilters(new HashSet<>());
        var itemModels =searchService.search(itemClass, PageRequest.ofSize(exportLimit), searchFormData,siteModel);
        //eğer limitten büyük veri varsa search servis çalışmaz
        if (itemModels.getTotalElements() > exportLimit) {
            throw new StoreRuntimeException("Data count is too big for search all.", "search.all.limit.message",
                    new Object[]{exportLimit, itemModels.getTotalElements()});
        }
        return List.of(modelMapper.map(itemModels.getContent(), SearchResultData[].class));
    }

    private void checkSqlInjection(String query) {

        var queryParts = Arrays.stream(query.split(StringUtils.SPACE)).map(String::toLowerCase).collect(Collectors.toSet());

        var sqlInjectionKeywords = Set.of("insert", "delete", "update", "drop", "create", "union", "fetch", "into", "values", "alter",
                "truncate", "grant", "revoke", "explain", "set", "limit", "offset", "exec", "execute",
                "declare", "cast", "convert", "waitfor", "sleep", "commit", "rollback", "begin",
                "ascii", "char", "character", "bin", "hex", "password", "dbms_pipe", "shutdown");

        Boolean error = queryParts.stream().anyMatch(sqlInjectionKeywords::contains);

        if (BooleanUtils.isTrue(error)) {
            throw new ModelReadException("Not authorization to run this operation.", "check.sql.reserved.keyword", null);
        }
    }
}
