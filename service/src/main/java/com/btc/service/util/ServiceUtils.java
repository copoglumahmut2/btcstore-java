package com.btc.service.util;

import com.btc.domain.data.extend.BaseData;
import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.extend.CodeBasedItemModel;
import com.btc.domain.model.custom.extend.ItemModel;
import com.btc.service.exception.model.ModelNotFoundException;
import constant.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import util.StoreClassUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class ServiceUtils {

    public static <T extends ItemModel> void checkItemModelIsExist(T t, Class tClass, SiteModel siteModel, String uniqueIdentifier) {
        if (Objects.isNull(t)) {
            var itemModelName = StoreClassUtils.getSimpleName(tClass);
            log.error("{} " + itemModelName + " not found on site {}", uniqueIdentifier, siteModel.getCode());
            throw new ModelNotFoundException(String.format("%s " + itemModelName + " not found on site", uniqueIdentifier, siteModel.getCode()),
                    MessageConstant.MODEL_NOT_FOUND, new Object[]{itemModelName, uniqueIdentifier, siteModel.getCode()});
        }
    }

    public static <T extends CodeBasedItemModel> T generateCodeIfMissing(T t) {
        t.setCode(StringUtils.isEmpty(t.getCode()) ? UUID.randomUUID().toString() : t.getCode());
        return t;
    }

    public static <T extends BaseData> boolean checkCodeIsNotEmpty(T t) {
        return Objects.nonNull(t) && StringUtils.isNotEmpty(t.getCode());
    }

    public static String formatDateForNotification(Date date) {
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}
