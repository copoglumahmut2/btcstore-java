package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.domain.model.custom.localize.LanguageModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import java.io.Serial;
import java.util.Set;

@Entity
@Table(name = DomainConstant.SITE_TABLE_NAME,
        indexes = {@Index(name = DomainConstant.SITE_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code")})
@FieldNameConstants
@Getter
@Setter
public class StoreSiteModel extends ItemModel {

    @Serial
    private static final long serialVersionUID = 1L;


    @Column(unique = true)
    private String code;

    private String name;

    @ElementCollection
    @CollectionTable(name = "site_domains", joinColumns = @JoinColumn(name = "site_id"))
    private Set<String> domains;

    @ManyToOne(fetch = FetchType.LAZY)
    private LanguageModel language;


}
