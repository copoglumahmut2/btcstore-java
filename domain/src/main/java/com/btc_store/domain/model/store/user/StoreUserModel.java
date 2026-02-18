package com.btc_store.domain.model.store.user;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.LanguageModel;
import com.btc_store.domain.model.custom.role.UserRoleModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = DomainConstant.USER_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(name = DomainConstant.USER_TABLE_NAME + DomainConstant.UNIQUE_KEYS,
                columnNames = {"username", SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.USER_TABLE_NAME + DomainConstant.USENAME_IDX, columnList = "username,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreUserModel extends CodeBasedItemModel {

    public static final String USER_RELATION = "user_id";

    @Serial
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private boolean passwordBlocked;

    private Date passwordBlockedDate;

    private boolean passwordChangeRequired;

    private Integer passwordBlockedAttempt = 0;

    private Date passwordLastChangeDate;

    private Integer unsuccessLoginAttempt = 0;

    private Date unsuccessLastLoginAttemptDate;

    private String unsuccessLoginClientInfo;

    private String description;

    private Boolean active = true;

    private Boolean deleted = false;

    private String email;

    private String phoneNumber;

    private Date lastLoginDate;

    private Double remainingLeaveHours = 0.0;

    @Transient
    private boolean passwordEncoded;

    @Transient
    private String definedPassword;

    private String jwtId;

    @ManyToMany
    @JoinTable(name = "user_roles_users",
            joinColumns = @JoinColumn(name = USER_RELATION), inverseJoinColumns = @JoinColumn(name = UserRoleModel.USER_ROLE_RELATION))
    private Set<UserRoleModel> userRoles;

    @ManyToOne(fetch = FetchType.LAZY)
    private LanguageModel language;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel picture;

    @ManyToMany
    @JoinTable(name = "user_groups_users",
            joinColumns = @JoinColumn(name = USER_RELATION), inverseJoinColumns = @JoinColumn(name = UserGroupModel.USER_GROUP_RELATION))
    private Set<UserGroupModel> userGroups;

    @PostLoad
    public void passwordEncoded() {
        this.setPasswordEncoded(true);
    }


}
