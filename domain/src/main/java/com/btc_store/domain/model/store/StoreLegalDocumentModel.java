package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.LegalDocumentType;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Date;

@Entity
@Table(name = DomainConstant.LEGAL_DOCUMENT_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.LEGAL_DOCUMENT_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreLegalDocumentModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String LEGAL_DOCUMENT_RELATION = "legal_document_id";

    @NotNull(message = "{backValidation.legalDocument.type.notNull}")
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private LegalDocumentType documentType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "title_tr", length = 500)),
            @AttributeOverride(name = "en", column = @Column(name = "title_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "title_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "title_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "title_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "title_it", length = 500))
    })
    private Localized title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "content_tr", columnDefinition = "TEXT")),
            @AttributeOverride(name = "en", column = @Column(name = "content_en", columnDefinition = "TEXT")),
            @AttributeOverride(name = "de", column = @Column(name = "content_de", columnDefinition = "TEXT")),
            @AttributeOverride(name = "fr", column = @Column(name = "content_fr", columnDefinition = "TEXT")),
            @AttributeOverride(name = "es", column = @Column(name = "content_es", columnDefinition = "TEXT")),
            @AttributeOverride(name = "it", column = @Column(name = "content_it", columnDefinition = "TEXT"))
    })
    private Localized content;

    @Column(name = "version", nullable = false, length = 20)
    private String version;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "is_current_version")
    private Boolean isCurrentVersion = true;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "short_text_tr", length = 1000)),
            @AttributeOverride(name = "en", column = @Column(name = "short_text_en", length = 1000)),
            @AttributeOverride(name = "de", column = @Column(name = "short_text_de", length = 1000)),
            @AttributeOverride(name = "fr", column = @Column(name = "short_text_fr", length = 1000)),
            @AttributeOverride(name = "es", column = @Column(name = "short_text_es", length = 1000)),
            @AttributeOverride(name = "it", column = @Column(name = "short_text_it", length = 1000))
    })
    private Localized shortText;

    @Column(nullable = false)
    private Boolean active = true;
}
