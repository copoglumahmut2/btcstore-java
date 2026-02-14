package com.btc.service.interceptor;

import com.btc.domain.enums.SearchOperator;
import com.btc.domain.model.custom.extend.ItemModel;
import com.btc.domain.model.custom.extend.SiteBasedItemModel;
import com.btc.domain.model.store.extend.StoreItemModel;
import com.btc.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class EntityModificationUtil implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;

    public static boolean isModified(ItemModel model, String field) {

        if (BooleanUtils.isFalse(model instanceof SiteBasedItemModel) ||
                model.isNewTransaction()) {
            return true;
        }

        /**
         * Eğer alan çoğul bir alan ise her zaman true dönülür.Çünkü liste bir alanı check etmek
         * performans açısından sorun oluşturabilir.
         */
        if (isFieldCollection(model, field)) {
            return true;
        }

        try {

            var searchService = applicationContext.getBean(SearchService.class);
            var dbValue = searchService.searchSingleResultField(model.getClass(),
                    Map.of(StoreItemModel.Fields.id, model.getId(),
                            StoreSiteBasedItemModel.Fields.site, ((SiteBasedItemModel) model).getSite()),
                    SearchOperator.AND, field);

            if (dbValue instanceof Object[]) {
                dbValue = ((Object[]) dbValue)[0];
            }
            var modifiedObj = PropertyUtils.getProperty(model, field);

            if (dbValue instanceof ItemModel) {
                return !Objects.equals(((ItemModel) dbValue).getId(), ((ItemModel) modifiedObj).getId());
            } else {
                return !Objects.equals(dbValue, modifiedObj);
            }

        } catch (Exception e) {
            return false;
        }
    }

    protected static boolean isFieldCollection(ItemModel model, String field) {
        try {
            var typeName = PropertyUtils.getPropertyDescriptor(model, field).getReadMethod().getGenericReturnType().getTypeName();
            return StringUtils.startsWith(typeName, "java.util");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            return false;
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
