package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.SectorModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DomainConstant.SUCCESS_STORY_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.SUCCESS_STORY_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreSuccessStoryModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String SUCCESS_STORY_RELATION = "success_story_id";

    @Column(name = "company", length = 500, nullable = false)
    private String company;

    @ManyToOne(fetch = FetchType.LAZY)
    private SectorModel sector;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "title_tr", length = 1000)),
            @AttributeOverride(name = "en", column = @Column(name = "title_en", length = 1000)),
            @AttributeOverride(name = "de", column = @Column(name = "title_de", length = 1000)),
            @AttributeOverride(name = "fr", column = @Column(name = "title_fr", length = 1000)),
            @AttributeOverride(name = "es", column = @Column(name = "title_es", length = 1000)),
            @AttributeOverride(name = "it", column = @Column(name = "title_it", length = 1000))
    })
    private Localized title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "html_content_tr", columnDefinition = "TEXT")),
            @AttributeOverride(name = "en", column = @Column(name = "html_content_en", columnDefinition = "TEXT")),
            @AttributeOverride(name = "de", column = @Column(name = "html_content_de", columnDefinition = "TEXT")),
            @AttributeOverride(name = "fr", column = @Column(name = "html_content_fr", columnDefinition = "TEXT")),
            @AttributeOverride(name = "es", column = @Column(name = "html_content_es", columnDefinition = "TEXT")),
            @AttributeOverride(name = "it", column = @Column(name = "html_content_it", columnDefinition = "TEXT"))
    })
    private Localized htmlContent;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel media;

    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @ElementCollection
    @CollectionTable(name = "success_story_results", joinColumns = @JoinColumn(name = "success_story_id"))
    @Column(name = "result", length = 500)
    private List<String> results = new ArrayList<>();

    @Column(name = "display_order")
    private Integer order;

    @Column(nullable = false)
    private Boolean active = true;
}
