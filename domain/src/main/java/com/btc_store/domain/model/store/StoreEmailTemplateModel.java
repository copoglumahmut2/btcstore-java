package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.EMAILTEMPLATEMODEL_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.EMAILTEMPLATEMODEL_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@FieldNameConstants
public class StoreEmailTemplateModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "{backValidation.emailTemplateModel.templateName.notEmpty}")
    @Column(name = "template_name", nullable = false)
    private String templateName;
    
    @Column(name = "related_item")
    private String relatedItem;

    @NotEmpty(message = "{backValidation.emailTemplateModel.subject.notEmpty}")
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotEmpty(message = "{backValidation.emailTemplateModel.body.notEmpty}")
    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(name = "description")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;
}
