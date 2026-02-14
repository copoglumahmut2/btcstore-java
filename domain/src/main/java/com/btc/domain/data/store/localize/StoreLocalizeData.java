package com.btc.domain.data.store.localize;

import com.btc.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;


@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = true)
public class StoreLocalizeData extends BaseData {

    public static final String TURKISH = "tr";
    public static final String ENGLISH = "en";
    public static final String SPANISH = "es";
    public static final String ITALIAN = "it";
    public static final String FRENCH = "fr";
    public static final String GERMAN = "de";

    @EqualsAndHashCode.Include
    private String tr;

    @EqualsAndHashCode.Include
    private String en;

    @EqualsAndHashCode.Include
    private String de;

    @EqualsAndHashCode.Include
    private String fr;

    @EqualsAndHashCode.Include
    private String es;

    @EqualsAndHashCode.Include
    private String it;

    public String getCurrentLanguageValue(String currentLanguage) {
        switch (currentLanguage) {
            case SPANISH:
                return getEs();
            case TURKISH:
                return getTr();
            case ITALIAN:
                return getIt();
            case FRENCH:
                return getFr();
            case GERMAN:
                return getDe();
            case ENGLISH:
                return getEn();
        }
        return StringUtils.EMPTY;
    }

}
