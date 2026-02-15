
package com.btc_store.persistence.dao.impl;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.data.custom.search.SearchFilter;
import com.btc_store.domain.data.custom.search.SearchFormData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.enums.SortDirection;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.persistence.dao.SearchDao;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import util.StoreClassUtils;
import util.StoreDateUtils;
import util.StoreStringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Transactional(readOnly = true,
        propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.READ_UNCOMMITTED)
public class SearchDaoImpl implements SearchDao {

    @PersistenceContext
    protected EntityManager entityManager;

    private static final String TENANT_MODEL = "TenantModel";
    private static final String SITE_MODEL = "SiteModel";


    @Override
    public <T extends ItemModel> T searchSingleResult(Class<T> tableClass, Map parameter, SearchOperator searchOperator) {
        Query query = createQuery(tableClass, parameter, searchOperator);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException(ExceptionUtils.getMessage(e)
                    + " Parameter : {" + parameter.toString() + "}");
        }

    }

    @Override
    public Object searchSingleResultField(Class<? extends ItemModel> tableClass, Map parameter, SearchOperator searchOperator, String... fields) {
        Query query = createQuery(tableClass, parameter, searchOperator, fields);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException(ExceptionUtils.getMessage(e)
                    + " Item : {" + StoreClassUtils.getSimpleName(tableClass) + "} Parameter : {" + parameter.toString() + "}");
        }
    }

    @Override
    public <T extends ItemModel> Collection<T> search(Class<T> tableClass, Map parameter, SearchOperator searchOperator) {
        Query query = createQuery(tableClass, parameter, searchOperator);
        return query.getResultList();
    }

    @Override
    public <T extends ItemModel> List<T> search(Class<T> tableClass, SearchFormData searchFormData, SiteModel siteModel) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(tableClass);
        Root<T> item = cq.from(tableClass);

        var predicates = searchFormData.getFilters().stream().map(searchFilter -> generatePredicate(searchFilter, item, cb)).collect(Collectors.toList());

        //always add site condition...
        predicates.add(cb.equal(item.get(StoreSiteBasedItemModel.Fields.site).get(SiteModel.Fields.code), siteModel.getCode()));
        cq = cq.select(item).where(predicates.toArray(Predicate[]::new));
        TypedQuery<T> typedQuery = entityManager.createQuery(cq);
        return typedQuery.getResultList();
    }

    @Override
    public <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, SearchFormData searchFormData, SiteModel siteModel) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Object.class);
        Root<T> item = cq.from(tableClass);

        List<Predicate> predicates;
        if (Objects.isNull(searchFormData) || CollectionUtils.isEmpty(searchFormData.getFilters())) {
            predicates = new ArrayList<>();
        } else {
            predicates = searchFormData.getFilters().stream().map(searchFilter -> generatePredicate(searchFilter, item, cb)).collect(Collectors.toList());
        }

        //site koşulu default olarak tüm sorgulara eklenir...
        predicates.add(cb.equal(item.get(StoreSiteBasedItemModel.Fields.site).get(StoreCodeBasedItemModel.Fields.code), siteModel.getCode()));

        cq = cq.distinct(true).where(predicates.toArray(Predicate[]::new));


        var sortQuery = false;
        if (Objects.nonNull(searchFormData.getSort()) && StringUtils.isNotEmpty(searchFormData.getSort().getField())) {
            cq.orderBy(Objects.equals(SortDirection.ASC, searchFormData.getSort().getDirection())
                    ? cb.asc(item.get(searchFormData.getSort().getField()))
                    : cb.desc(item.get(searchFormData.getSort().getField())));
            cq.multiselect(item.get("id"), item.get(searchFormData.getSort().getField()));
            sortQuery = true;
        } else {
            cq.multiselect(item.get("id"));
        }

        TypedQuery typedQuery = entityManager.createQuery(cq);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        /*
        sorgu sonucuna filtrede gelmesi beklenen kayıtların idleri bulunur ve bu idlere göre
        ikinci bir sorgu çalıştırılarak sonuç dönülür. MSSQL kaynaklı bir distinct problemi nedeni ile bu kısım
        bu şekilde editlendi.
        @bknz : com.microsoft.sqlserver.jdbc.SQLServerException:
         The text data type cannot be selected as DISTINCT because it is not comparable.
         */
        var ids = typedQuery.getResultList();
        var availableIds = new LinkedHashSet<Long>();
        if (BooleanUtils.isTrue(sortQuery)) {
            for (Object a : ids) {
                var b = (Object[]) a;
                availableIds.add((Long) b[0]);
            }
        } else {
            availableIds.addAll(CollectionUtils.isEmpty(ids) ? List.of(Long.valueOf(0)) : ids);
        }

        //gelen idlere göre sonuç dönülür...

        CriteriaQuery cqForId = cb.createQuery(tableClass);
        Root<T> itemForId = cqForId.from(tableClass);

        cqForId = cqForId.select(itemForId).where(itemForId.get("id").in(availableIds));

        if (Objects.nonNull(searchFormData.getSort()) && StringUtils.isNotEmpty(searchFormData.getSort().getField())) {
            cqForId.orderBy(Objects.equals(SortDirection.ASC, searchFormData.getSort().getDirection())
                    ? cb.asc(itemForId.get(searchFormData.getSort().getField()))
                    : cb.desc(itemForId.get(searchFormData.getSort().getField())));

        }
        typedQuery = entityManager.createQuery(cqForId);


        //sorgu sonucuna göre toplam count bulunur....
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        var root = countQuery.from(tableClass);
        countQuery.select(cb.countDistinct(root));
        
        List<Predicate> countPredicates;
        if (Objects.isNull(searchFormData) || CollectionUtils.isEmpty(searchFormData.getFilters())) {
            countPredicates = new ArrayList<>();
        } else {
            countPredicates = searchFormData.getFilters().stream()
                    .map(searchFilter -> generatePredicate(searchFilter, root, cb))
                    .collect(Collectors.toList());
        }
        countPredicates.add(cb.equal(root.get(StoreSiteBasedItemModel.Fields.site).get(StoreCodeBasedItemModel.Fields.code), siteModel.getCode()));
        countQuery.where(countPredicates.toArray(Predicate[]::new));
        long count = entityManager.createQuery(countQuery).getSingleResult();
        var result = typedQuery.getResultList().stream().distinct().toList();
        return new PageImpl<>(result, pageable, count);
    }

    @SneakyThrows
    Predicate generatePredicate(SearchFilter filter, Root item, CriteriaBuilder cb) {
        Predicate predicate = null;
        var javaType = item.get(filter.getName()).getJavaType();
        var enumList = new ArrayList<>();
        if (javaType.isEnum()) {
            //if value is multiple...
            var enums = Arrays.asList(javaType.getEnumConstants());
            if (CollectionUtils.isNotEmpty(filter.getValues())) {
                filter.getValues().forEach(en -> {
                    var enumValue = enums
                            .stream().filter(e -> StringUtils.equalsIgnoreCase(String.valueOf(e), String.valueOf(en)))
                            .findFirst().orElseThrow(NoSuchFieldError::new);
                    enumList.add(enumValue);
                });

            } else {
                var enumValue = enums
                        .stream().filter(e -> StringUtils.equalsIgnoreCase(String.valueOf(e), String.valueOf(filter.getValue())))
                        .findFirst().orElseThrow(NoSuchFieldError::new);
                enumList.add(enumValue);
            }

            var in = cb.in(item.get(filter.getName()));
            enumList.forEach(en -> in.value(en));

            switch (filter.getSearchCondition()) {
                case EQUALS, CONTAINS -> predicate = in;
                case NOT_EQUALS, NOTCONTAINS -> predicate = in.not();
            }


        } else {
            switch (ClassUtils.getSimpleName(javaType)) {
                case "Boolean":
                case "boolean":
                    Expression<Boolean> booleanExpression = item.get(filter.getName());
                    predicate = BooleanUtils.toBoolean(filter.getValue().toString()) ? cb.isTrue(booleanExpression) : cb.isFalse(booleanExpression);
                    break;
                case "int":
                case "Integer":
                case "double":
                case "Double":
                case "long":
                case "Long":
                case "float":
                case "Float":
                case "BigDecimal":
                    switch (filter.getSearchCondition()) {
                        case BETWEEN -> {
                            var minMaxValues = StringUtils.split(String.valueOf(filter.getValue()), "-");
                            var minVal = NumberUtils.toScaledBigDecimal(minMaxValues[0]);
                            var maxVal = NumberUtils.toScaledBigDecimal(minMaxValues[1]);
                            predicate = cb.between(item.get(filter.getName()), minVal, maxVal);
                        }
                        case EQUALS -> {
                            Expression<Number> numericExpression = item.get(filter.getName());
                            predicate = cb.equal(numericExpression, filter.getValue());
                        }
                        case NOT_EQUALS -> {
                            Expression<Number> numericExpression = item.get(filter.getName());
                            predicate = cb.equal(numericExpression, filter.getValue()).not();
                        }
                        case LESSOREQUAL -> {
                            Expression<Number> numericExpression = item.get(filter.getName());
                            predicate = cb.le(numericExpression, NumberUtils.createNumber(String.valueOf(filter.getValue())));
                        }
                        case GREATEROREQUAL -> {
                            Expression<Number> numericExpression = item.get(filter.getName());
                            predicate = cb.ge(numericExpression, NumberUtils.createNumber(String.valueOf(filter.getValue())));
                        }

                        case ISEMPTY -> predicate = cb.isNull(item.get(filter.getName()));
                        case ISNOTEMPTY -> predicate = cb.isNotNull(item.get(filter.getName()));

                    }

                    break;
                case "Localized":
                    switch (filter.getSearchCondition()) {
                        case EQUALS:
                            var givenPredicate = cb.equal(cb.lower(item.get(filter.getName()).get(filter.getLocale()))
                                    , StringUtils.lowerCase(filter.getValue().toString(), Locale.forLanguageTag(filter.getLocale())));
                            var turkishPredicate = cb.equal(cb.lower(item.get(filter.getName()).get(filter.getLocale()))
                                    , StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()), Locale.forLanguageTag(filter.getLocale())));

                            predicate = cb.or(givenPredicate, turkishPredicate);
                            break;

                        case LIKE:
                            givenPredicate = cb.like(cb.lower(item.get(filter.getName()).get(filter.getLocale())),
                                    "%" + StringUtils.lowerCase(filter.getValue().toString(), Locale.forLanguageTag(filter.getLocale())) + "%");

                            turkishPredicate = cb.like(cb.lower(item.get(filter.getName()).get(filter.getLocale())),
                                    "%" + StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()), Locale.forLanguageTag(filter.getLocale())) + "%");

                            predicate = cb.or(givenPredicate, turkishPredicate);

                            break;
                        case STARTS_WITH:
                            givenPredicate = cb.like(cb.lower(item.get(filter.getName()).get(filter.getLocale())),
                                    StringUtils.lowerCase(filter.getValue().toString(), Locale.forLanguageTag(filter.getLocale())) + "%");
                            turkishPredicate = cb.like(cb.lower(item.get(filter.getName()).get(filter.getLocale())),
                                    StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()),
                                            Locale.forLanguageTag(filter.getLocale())) + "%");

                            predicate = cb.or(givenPredicate, turkishPredicate);
                            break;
                        case ISEMPTY:
                            predicate = cb.isNull(item.get(filter.getName()).get(filter.getLocale()));
                            break;

                        case ISNOTEMPTY:
                            predicate = cb.isNotNull(item.get(filter.getName()).get(filter.getLocale()));
                            break;
                        default:
                            givenPredicate = cb.equal(item.get(filter.getName()).get(filter.getLocale()), filter.getValue());
                            turkishPredicate = cb.equal(item.get(filter.getName()).get(filter.getLocale()), Objects.isNull(filter.getValue()) ? filter.getValue() :
                                    StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()));
                            predicate = cb.or(givenPredicate, turkishPredicate);
                            break;


                    }
                    break;
                case "Set":
                case "List":
                    CriteriaBuilder.In in;
                    if (Objects.nonNull(filter.getChild())) {
                        if (StringUtils.isNotEmpty(filter.getChild().getRelationField())) {
                            in = cb.in(item.join(filter.getName()).get(filter.getChild().getName())
                                    .get(filter.getChild().getRelationField()));
                            if (CollectionUtils.isNotEmpty(filter.getChild().getValues())) {
                                filter.getChild().getValues().forEach(v -> in.value(v));
                            } else {
                                in.value(filter.getChild().getValue());
                            }
                            switch (filter.getChild().getSearchCondition()) {
                                case CONTAINS, EQUALS -> predicate = in;
                                case NOTCONTAINS -> predicate = in.not();
                            }
                        } else {
                            if (Objects.nonNull(filter.getChild().getValue())) {
                                //Eğer child alanın relationfielddı yok elementcolletion gibi alanlarda gönderilen
                                //value değeri liste alan içersinde var mı diye bakılıyor
                                predicate = cb.isMember(filter.getChild().getValue(), item.join(filter.getName()).get(filter.getChild().getName()));
                            } else {
                                //Eğer child alanın relationfielddı yok elementcolletion gibi alanlarda gönderilen
                                //values değerindeki alanlar liste alan içersinde var mı diye bakılıyor
                                Predicate combinedPredicate = cb.conjunction();
                                for (Object value : filter.getChild().getValues()) {
                                    combinedPredicate = cb.or(combinedPredicate, cb.isMember(value, item.join(filter.getName()).get(filter.getChild().getName())));
                                }
                                predicate = combinedPredicate;
                            }
                        }
                    } else {
                        if (StringUtils.isNotEmpty(filter.getRelationField())) {
                            in = cb.in(item.join(filter.getName()).get(filter.getRelationField()));
                            filter.getValues().forEach(v -> in.value(v));
                            switch (filter.getSearchCondition()) {
                                case CONTAINS -> predicate = in;
                                case NOTCONTAINS -> predicate = in.not();
                            }
                        } else {
                            if (Objects.nonNull(filter.getValue())) {
                                //elementcolletion gibi alanlarda gönderilen
                                //value değeri liste alan içersinde var mı diye bakılıyor
                                predicate = cb.isMember(filter.getValue(), item.get(filter.getName()));
                            } else {
                                //Eğer alanın relationfielddı yok elementcolletion gibi alanlarda gönderilen
                                //values değerindeki alanlar liste alan içersinde var mı diye bakılıyor
                                Predicate combinedPredicate = cb.conjunction();
                                for (Object value : filter.getValues()) {
                                    combinedPredicate = cb.or(combinedPredicate, cb.isMember(value, item.get(filter.getName())));
                                }
                                predicate = combinedPredicate;
                            }
                        }
                    }
                    break;
                case "Date":
                    switch (filter.getSearchCondition()) {
                        case GREATER:
                            predicate = cb.greaterThan(item.get(filter.getName()).as(Date.class), StoreDateUtils.minTime(filter.getDate()));
                            break;
                        case LESS:
                            predicate = cb.lessThan(item.get(filter.getName()).as(Date.class), StoreDateUtils.maxTime(filter.getDate()));
                            break;
                        case LESSOREQUAL:
                            predicate = cb.lessThanOrEqualTo(item.get(filter.getName()).as(Date.class), StoreDateUtils.maxTime(filter.getDate()));
                            break;
                        case GREATEROREQUAL:
                            predicate = cb.greaterThanOrEqualTo(item.get(filter.getName()).as(Date.class), StoreDateUtils.minTime(filter.getDate()));
                            break;
                        case ISEMPTY:
                            predicate = cb.isNull(item.get(filter.getName()));
                            break;

                        case ISNOTEMPTY:
                            predicate = cb.isNotNull(item.get(filter.getName()));
                            break;

                        case EQUALS:
                            predicate = cb.equal(item.get(filter.getName()).as(Date.class), StoreDateUtils.minTime(filter.getDate()));
                            break;

                        case BETWEEN:
                            var minMaxValues = StringUtils.split(String.valueOf(filter.getValue()), "-");
                            var minDate = DateUtils.parseDate(minMaxValues[0], "dd.MM.yyyy");
                            var maxDate = DateUtils.parseDate(minMaxValues[1], "dd.MM.yyyy");
                            predicate = cb.between(item.get(filter.getName()).as(Date.class), StoreDateUtils.minTime(minDate),
                                    StoreDateUtils.maxTime(maxDate));
                            break;

                        default:
                            predicate = cb.equal(item.get(filter.getName()).as(Date.class), StoreDateUtils.minTime(filter.getDate()));
                            break;
                    }
                    break;

                default:
                    if (Objects.nonNull(filter.getChild())) {
                        if (StringUtils.isNotEmpty(filter.getChild().getRelationField())) {

                            var type = item.join(filter.getName()).get(filter.getChild().getName()).getJavaType();
                            var className = ClassUtils.getSimpleName(type);

                            if (StringUtils.equals("Set", className) || StringUtils.equals("List", className)) {
                                var childIn = cb.in(item.join(filter.getName())
                                        .join(filter.getChild().getName())
                                        .get(filter.getChild().getRelationField()));

                                filter.getChild().getValues().forEach(v -> childIn.value(v));

                                switch (filter.getChild().getSearchCondition()) {
                                    case CONTAINS, IN:
                                        predicate = childIn;
                                        break;
                                    case NOTCONTAINS:
                                        predicate = childIn.not();
                                        break;
                                }


                            } else {

                                if (Objects.nonNull(filter.getChild().getValue())) {
                                    predicate = cb.equal(item.join(filter.getName())
                                            .get(filter.getChild().getName())
                                            .get(filter.getChild().getRelationField()), filter.getChild().getValue());

                                } else if (CollectionUtils.isNotEmpty(filter.getChild().getValues())) {

                                    var childIn = cb.in(item.join(filter.getName())
                                            .join(filter.getChild().getName())
                                            .get(filter.getChild().getRelationField()));

                                    filter.getChild().getValues().forEach(v -> childIn.value(v));

                                    switch (filter.getChild().getSearchCondition()) {
                                        case CONTAINS, IN:
                                            predicate = childIn;
                                            break;
                                        case NOTCONTAINS:
                                            predicate = childIn.not();
                                            break;
                                    }


                                }


                            }


                        } else {
                            predicate = cb.equal(item.join(filter.getName()).get(filter.getChild().getName()), filter.getChild().getValue());
                        }

                    } else if (StringUtils.isNotEmpty(filter.getRelationField())) {


                        switch (filter.getSearchCondition()) {
                            case CONTAINS:
                                CriteriaBuilder.In contains = cb.in(item.join(filter.getName()).get(filter.getRelationField()));
                                filter.getValues().forEach(contains::value);
                                predicate = contains;
                                break;
                            case NOTCONTAINS:
                                CriteriaBuilder.In notContains = cb.in(item.join(filter.getName()).get(filter.getRelationField()));
                                filter.getValues().forEach(notContains::value);
                                predicate = notContains.not();
                                break;
                            case GREATER:
                                predicate = cb.greaterThan(item.join(filter.getName()).get(filter.getRelationField()).as(Date.class), StoreDateUtils.minTime(filter.getDate()));
                                break;
                            case LESS:
                                predicate = cb.lessThan(item.join(filter.getName()).get(filter.getRelationField()).as(Date.class), StoreDateUtils.maxTime(filter.getDate()));
                                break;
                            case LESSOREQUAL:
                                predicate = cb.lessThanOrEqualTo(item.join(filter.getName()).get(filter.getRelationField()).as(Date.class), StoreDateUtils.maxTime(filter.getDate()));
                                break;
                            case GREATEROREQUAL:
                                predicate = cb.greaterThanOrEqualTo(item.join(filter.getName()).get(filter.getRelationField()).as(Date.class), StoreDateUtils.minTime(filter.getDate()));
                                break;
                            default:
                                predicate = cb.equal(item.join(filter.getName()).get(filter.getRelationField()), filter.getValue());
                                break;
                        }


                    } else {
                        switch (filter.getSearchCondition()) {
                            case EQUALS:

                                var givenPredicate = cb.equal(cb.lower(item.get(filter.getName()))
                                        , StringUtils.lowerCase(filter.getValue().toString(), DomainConstant.TURKISH));

                                var turkishPredicate = cb.equal(cb.lower(item.get(filter.getName()))
                                        , StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()),
                                                DomainConstant.TURKISH));
                                predicate = cb.or(givenPredicate, turkishPredicate);
                                break;

                            case NOT_EQUALS:
                                givenPredicate = cb.equal(cb.lower(item.get(filter.getName()))
                                        , StringUtils.lowerCase(filter.getValue().toString(), DomainConstant.TURKISH));

                                turkishPredicate = cb.equal(cb.lower(item.get(filter.getName()))
                                        , StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()),
                                                DomainConstant.TURKISH));
                                predicate = cb.or(givenPredicate, turkishPredicate).not();
                                break;

                            case LIKE:

                                givenPredicate = cb.like(cb.lower(item.get(filter.getName())),
                                        "%" + StringUtils.lowerCase(filter.getValue().toString(), DomainConstant.TURKISH) + "%");

                                turkishPredicate = cb.like(cb.lower(item.get(filter.getName())),
                                        "%" + StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()),
                                                DomainConstant.TURKISH) + "%");
                                predicate = cb.or(givenPredicate, turkishPredicate);
                                break;
                            case STARTS_WITH:
                                givenPredicate = cb.like(cb.lower(item.get(filter.getName())),
                                        StringUtils.lowerCase(filter.getValue().toString(), DomainConstant.TURKISH) + "%");
                                turkishPredicate = cb.like(cb.lower(item.get(filter.getName())),
                                        StringUtils.lowerCase(StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()),
                                                DomainConstant.TURKISH) + "%");
                                predicate = cb.or(givenPredicate, turkishPredicate);
                                break;
                            case ISEMPTY:
                                predicate = cb.or(
                                        cb.isNull(item.get(filter.getName())),
                                        cb.equal(item.get(filter.getName()), StringUtils.EMPTY)
                                );
                                break;

                            case ISNOTEMPTY:
                                predicate = cb.and(
                                        cb.isNotNull(item.get(filter.getName())),
                                        cb.notEqual(item.get(filter.getName()), StringUtils.EMPTY)
                                );
                                break;
                            case NOTNULL:
                                predicate = cb.and(
                                        cb.isNotNull(item.get(filter.getName()))
                                );
                                break;
                            case IN, CONTAINS:
                                predicate = cb.in(item.get(filter.getName())).value(filter.getValues());
                                break;

                            case NOTCONTAINS:
                                var notContains = cb.in(item.get(filter.getName())).value(filter.getValues());
                                predicate = notContains.not();
                                break;
                            default:
                                givenPredicate = cb.equal(item.get(filter.getName()), filter.getValue());
                                turkishPredicate = cb.equal(item.get(filter.getName()), Objects.isNull(filter.getValue()) ? filter.getValue()
                                        : StoreStringUtils.convertTurkishCharacter(filter.getValue().toString()));
                                predicate = cb.or(givenPredicate, turkishPredicate);
                                break;
                        }
                    }

                    break;

            }
        }


        return predicate;
    }

    @Override
    public <T extends ItemModel> Page<T> search(Class<T> tableClass, Pageable pageable, String query, Map parameter) {

        checkRestrictionClassForParam(tableClass, parameter);

        var querySplit = query.split("from");
        //Burada dinamik searchda from CartModel c gibi olan sorguda c kısmını yakalamak için yapılmıştır.
        var modelSplit = querySplit[1].trim().split(StringUtils.SPACE);
        var countQuery = "select count (distinct ".concat(modelSplit[1]).concat(") from ").concat(querySplit[1].split("group by | order by")[0]);

        TypedQuery<T> typedQuery = entityManager.createQuery(query, tableClass);
        if (MapUtils.isNotEmpty(parameter)) {
            parameter.keySet().forEach(p -> typedQuery.setParameter(p.toString(), parameter.get(p)));
        }

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        var countTypedQuery = entityManager.createQuery(countQuery);
        if (MapUtils.isNotEmpty(parameter)) {
            parameter.keySet().forEach(p -> countTypedQuery.setParameter(p.toString(), parameter.get(p)));
        }


        return new PageImpl<>(typedQuery.getResultList(), pageable, (long) countTypedQuery.getSingleResult());
    }


    @Override
    public <T extends ItemModel> List<T> search(Class<T> tableClass, String query, Map parameter) {

        checkRestrictionClassForParam(tableClass, parameter);

        TypedQuery<? extends ItemModel> typedQuery = entityManager.createQuery(query, tableClass);
        if (MapUtils.isNotEmpty(parameter)) {
            parameter.keySet().forEach(p -> typedQuery.setParameter(p.toString(), parameter.get(p)));
        }
        return (List<T>) typedQuery.getResultList();
    }

    @Override
    public <T extends ItemModel> T searchSingleResultRelation(T t, String... relation) {

        if (ArrayUtils.isEmpty(relation)) {
            throw new IllegalArgumentException("relation can be null or empty");
        }

        var validRelationRequest = new ArrayList<String>();
        PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        for (String r : relation) {
            if (BooleanUtils.isFalse(unitUtil.isLoaded(t, r))) {
                validRelationRequest.add(r);
            }
        }

        if (CollectionUtils.isNotEmpty(validRelationRequest)) {
            EntityGraph entityGraph = entityManager.createEntityGraph(t.getClass());
            entityGraph.addAttributeNodes(validRelationRequest.toArray(String[]::new));

            Map<String, Object> hints = new HashMap();
            hints.put(org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH.getKey(), entityGraph);
            var relationItem = (T) entityManager.find(t.getClass(), t.getId(), hints);

            validRelationRequest.forEach(r -> {
                try {
                    PropertyUtils.setProperty(t, r, PropertyUtils.getProperty(relationItem, r));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return t;

    }

    @SneakyThrows
    private Query createQuery(Class itemClass, Map params, SearchOperator searchOperator, String... fields) {

        checkRestrictionClassForParam(itemClass, params);

        final StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("select ");
        if (ArrayUtils.isNotEmpty(fields)) {
            var loopIndex = 1;
            for (var field : fields) {
                //son loop ise query sonuna virgül koyma
                if (loopIndex == fields.length) {
                    queryBuilder.append("m" + "." + field);
                } else {
                    queryBuilder.append("m" + "." + field + ", ");
                }
                loopIndex++;
            }
        } else {
            queryBuilder.append("m");
        }
        queryBuilder.append(" from ").append(itemClass.getSimpleName()).append(" m ");

        var nullFields = new HashMap<>();
        if (MapUtils.isNotEmpty(params)) {

            params.keySet().forEach(e -> {
                if (Objects.isNull(params.get(e))) {
                    nullFields.put(e, params.get(e));
                }
            });

            if (MapUtils.isNotEmpty(nullFields)) {
                nullFields.forEach((key, value) -> params.remove(key, nullFields.get(key)));
            }
        }

        if (MapUtils.isNotEmpty(params)) {
            queryBuilder.append("WHERE ");
            params.keySet().forEach(e -> queryBuilder.append("m.").append(e).append("=").append(":" + e).append(StringUtils.SPACE)
                    .append(searchOperator.getValue()).append(StringUtils.SPACE));
            queryBuilder.replace(queryBuilder.length() - StringUtils.length(searchOperator.getValue()) - 1, queryBuilder.length(), StringUtils.EMPTY);
        }

        if (MapUtils.isNotEmpty(nullFields)) {
            nullFields.keySet().forEach(p -> queryBuilder.append(searchOperator.getValue()).append(StringUtils.SPACE).append(p).append(StringUtils.SPACE).append(" is null "));
        }

        TypedQuery query;
        if (ArrayUtils.isNotEmpty(fields)) {
            query = entityManager.createQuery(queryBuilder.toString(), Object[].class);
        } else {
            query = entityManager.createQuery(queryBuilder.toString(), itemClass);
        }


        if (!params.isEmpty()) {

            params.keySet().forEach(e -> query.setParameter(e.toString(), params.get(e)));

        }
        return query;
    }

    protected void checkRestrictionClassForParam(Class itemClass, Map params) {
        //all queries must contains site parameter...
        if (!StringUtils.equals(StoreClassUtils.getSimpleName(itemClass), SITE_MODEL)
                && !StringUtils.equals(StoreClassUtils.getSimpleName(itemClass), TENANT_MODEL)
                && BooleanUtils.isFalse(params.containsKey("site"))) {
            throw new IllegalArgumentException("Query must contain 'site' parameter");
        }
    }

    @SneakyThrows
    protected Class getFieldType(Class tClass, String field) {
        var superClass = tClass.getSuperclass();
        var classFields = new ArrayList<>(Arrays.asList(tClass.getDeclaredFields()));
        while (Objects.nonNull(superClass)) {
            var superClassFields = new ArrayList<>(Arrays.asList(superClass.getDeclaredFields()));
            classFields.addAll(superClassFields);
            superClass = superClass.getSuperclass();
        }

        var fieldType = classFields
                .stream()
                .filter(f -> StringUtils.equals(f.getName(), field))
                .findFirst().orElseThrow(() -> new NoSuchFieldException(String.format("No such field on this item %s - %s ",
                        tClass.getSimpleName(), field)));

        return StoreClassUtils.primitiveToWrapper(fieldType.getType());
    }

    @Override
    public Map<List<String>, Page<Map<String, Object>>> searchWithNativeSql(Pageable pageable, String querySql) throws SQLException {

        var session = entityManager.unwrap(Session.class);
        var connection = session.doReturningWork(conn -> conn);
        var stmt = connection.createStatement();
        stmt.setFetchSize(pageable.getPageSize());
        var rs = stmt.executeQuery(querySql);

        var rsmd = rs.getMetaData();
        var columnCount = rsmd.getColumnCount();
        var columnNames = new ArrayList<String>();
        for (var i = 1; i <= columnCount; i++) {
            columnNames.add(rsmd.getColumnName(i));
        }

        var results = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            var row = new HashMap<String, Object>();
            for (var columnName : columnNames) {
                var columnValue = rs.getObject(columnName);
                if (columnValue == null) {
                    columnValue = "null";
                }
                row.put(columnName, columnValue);
            }
            results.add(row);
        }

        var resultMap = new HashMap<List<String>, Page<Map<String, Object>>>();
        resultMap.put(columnNames, new PageImpl<>(results, pageable, results.size()));
        return resultMap;
    }

    @Override
    public Set<String> getAllTableNames() {
        var session = entityManager.unwrap(Session.class);
        var sessionFactory = session.getSessionFactory();

        var tableNames = new HashSet<String>();
        var entityPersisters = ((MetamodelImplementor) sessionFactory.getMetamodel()).entityPersisters();
        entityPersisters.forEach((entityName, entityPersister) -> {
            if (entityPersister instanceof SingleTableEntityPersister) {
                tableNames.add(((SingleTableEntityPersister) entityPersister).getTableName().replaceAll("\"", StringUtils.EMPTY));
            }
        });
        return new TreeSet<>(tableNames);
    }

}
