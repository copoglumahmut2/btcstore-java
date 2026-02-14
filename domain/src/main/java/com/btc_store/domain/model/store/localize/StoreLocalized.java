package com.btc_store.domain.model.store.localize;

import com.btc_store.domain.constant.DomainConstant;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@ToString
public class StoreLocalized implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tr;

    private String en;

    private String de;

    private String fr;

    private String es;

    private String it;


    public StoreLocalized(Locale locale, String value) {
        setValue(locale, value);
    }

    public void setValue(Locale locale, String value) {
        if (Locale.ENGLISH.equals(locale)) {
            setEn(value);
        } else if (Locale.GERMAN.equals(locale)) {
            setDe(value);
        } else if (DomainConstant.TURKISH.equals(locale)) {
            setTr(value);
        } else if (Locale.FRENCH.equals(locale)) {
            setFr(value);
        } else if (DomainConstant.SPANISH.equals(locale)) {
            setEs(value);
        } else if (Locale.ITALIAN.equals(locale)) {
            setIt(value);
        }
    }

    public String getValue(Locale locale) {
        if (Locale.ENGLISH.equals(locale)) {
            return getEn();
        } else if (Locale.GERMAN.equals(locale)) {
            return getDe();
        } else if (DomainConstant.TURKISH.equals(locale)) {
            return getTr();
        } else if (Locale.FRENCH.equals(locale)) {
            return getFr();
        } else if (DomainConstant.SPANISH.equals(locale)) {
            return getEs();
        } else if (Locale.ITALIAN.equals(locale)) {
            return getIt();
        }
        return getTr();
    }

}
