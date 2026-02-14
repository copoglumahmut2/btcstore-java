package com.btc.domain.model.store.role;

import com.btc.domain.constant.DomainConstant;
import com.btc.domain.model.custom.extend.CodeBasedItemModel;
import com.btc.domain.model.custom.extend.SiteBasedItemModel;
import com.btc.domain.model.custom.localize.Localized;
import com.btc.domain.model.custom.user.UserGroupModel;
import com.btc.domain.model.custom.user.UserModel;
import com.btc.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Set;

@Entity
@Table(name = DomainConstant.USERROLE_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.USERROLE_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreUserRoleModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String USER_ROLE_RELATION = "user_role_id";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "description_tr", length = 5000)),
            @AttributeOverride(name = "en", column = @Column(name = "description_en", length = 5000)),
            @AttributeOverride(name = "de", column = @Column(name = "description_de", length = 5000)),
            @AttributeOverride(name = "fr", column = @Column(name = "description_fr", length = 5000)),
            @AttributeOverride(name = "es", column = @Column(name = "description_es", length = 5000)),
            @AttributeOverride(name = "it", column = @Column(name = "description_it", length = 5000))
    })
    private Localized description;


    @ManyToMany
    @JoinTable(name = "user_roles_user_groups",
            joinColumns = @JoinColumn(name = USER_ROLE_RELATION), inverseJoinColumns = @JoinColumn(name = UserGroupModel.USER_GROUP_RELATION))
    private Set<UserGroupModel> userGroups;

    @ManyToMany
    @JoinTable(name = "user_roles_users",
            joinColumns = @JoinColumn(name = USER_ROLE_RELATION), inverseJoinColumns = @JoinColumn(name = UserModel.USER_RELATION))
    private Set<UserModel> users;


    private Boolean active = Boolean.TRUE;
}
