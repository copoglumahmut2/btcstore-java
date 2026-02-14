package com.btc_store.domain.model.store.user;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.custom.role.UserRoleModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Set;

@Entity
@Table(name = DomainConstant.USERGROUP_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(name = DomainConstant.USERGROUP_TABLE_NAME + DomainConstant.UNIQUE_KEYS,
                columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.USERGROUP_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreUserGroupModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String USER_GROUP_RELATION = "user_group_id";

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

    @ManyToMany
    @JoinTable(name = "user_groups_users",
            joinColumns = @JoinColumn(name = USER_GROUP_RELATION), inverseJoinColumns = @JoinColumn(name = UserModel.USER_RELATION))
    private Set<UserModel> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles_user_groups",
            joinColumns = @JoinColumn(name = USER_GROUP_RELATION), inverseJoinColumns = @JoinColumn(name = UserRoleModel.USER_ROLE_RELATION))
    private Set<UserRoleModel> userRoles;
}
