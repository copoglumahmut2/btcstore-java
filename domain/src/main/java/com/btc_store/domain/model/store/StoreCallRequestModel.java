package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.LegalDocumentModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = DomainConstant.CALLREQUESTMODEL_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.CALLREQUESTMODEL_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@FieldNameConstants
public class StoreCallRequestModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "{backValidation.callRequestModel.customerName.notEmpty}")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotEmpty(message = "{backValidation.callRequestModel.customerEmail.notEmpty}")
    @Email(message = "{backValidation.callRequestModel.customerEmail.invalid}")
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @NotEmpty(message = "{backValidation.callRequestModel.customerPhone.notEmpty}")
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private CallRequestPriority priority = CallRequestPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private CallRequestStatus status = CallRequestStatus.PENDING;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "call_request_assigned_groups",
        joinColumns = @JoinColumn(name = "call_request_id"),
        inverseJoinColumns = @JoinColumn(name = "user_group_id")
    )
    private Set<UserGroupModel> assignedGroups = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "call_request_assigned_users",
        joinColumns = @JoinColumn(name = "call_request_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> assignedUsers = new HashSet<>();

    private Date completedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private LegalDocumentModel acceptedLegalDocument;
}
