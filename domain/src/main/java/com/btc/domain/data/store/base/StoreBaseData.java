package com.btc.domain.data.store.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StoreBaseData implements Serializable {

    @EqualsAndHashCode.Include
    private Object id;
    @EqualsAndHashCode.Include
    private String code;
    private String isoCode;
    //task  adımı için eklendi.Kontrol adımlarında uygulanacak.
    private boolean taskStep;

    @JsonIgnore
    public boolean isNew() {
        return Objects.isNull(this.id) || (this.id instanceof Long ? Long.valueOf(((Long) this.id).longValue()) == 0
                : this.id instanceof String ? StringUtils.isEmpty(this.id.toString()) : false);
    }
}

