package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.DataType;
import com.btc_store.domain.enums.ParameterType;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import java.io.Serial;

@Entity
@Table(name = DomainConstant.PARAMETERMODEL_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.PARAMETERMODEL_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@FieldNameConstants
public class StoreParameterModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String PARAMETER_RELATION = "parameter_id";

    @NotEmpty(message = "{backValidation.parameterModel.value.notEmpty}")
    private String value;

    @Enumerated(EnumType.STRING)
    private DataType dataType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "description_tr", length = 500)),
            @AttributeOverride(name = "en", column = @Column(name = "description_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "description_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "description_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "description_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "description_it", length = 500))
    })
    private Localized description;

    @Enumerated(EnumType.STRING)
    private ParameterType parameterType;

    private Boolean encrypt;

    @Transient
    private String encryptedValue;

    @PostLoad
    public void encryptedValue() {
        if(Boolean.TRUE.equals(this.encrypt)) {
            this.setEncryptedValue(this.getValue());
        }
    }


}
