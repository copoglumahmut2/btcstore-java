package com.btc.domain.model.store.media;

import com.btc.domain.constant.DomainConstant;
import com.btc.domain.model.custom.CmsCategoryModel;
import com.btc.domain.model.custom.extend.CodeBasedItemModel;
import com.btc.domain.model.custom.extend.SiteBasedItemModel;
import com.btc.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.MEDIA_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.MEDIA_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
@FieldNameConstants
public class StoreMediaModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String MEDIA_RELATION = "media_id";

    private String realFileName;

    private String encodedFileName;

    private String filePath;

    private String rootPath;

    private String servePath;

    private String absolutePath;

    private String mime;

    private String extension;

    private long size;

    private boolean secure;

    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private CmsCategoryModel cmsCategory;
}
