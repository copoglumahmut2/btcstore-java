package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreItemModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Date;

@Entity
@Table(name = DomainConstant.CALLREQUESTMODEL_TABLE_NAME,
        indexes = {
                @Index(name = DomainConstant.CALLREQUESTMODEL_TABLE_NAME + "_status_idx", columnList = "status"),
                @Index(name = DomainConstant.CALLREQUESTMODEL_TABLE_NAME + "_site_idx", columnList = "site_id")
        })
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
    private CallRequestStatus status = CallRequestStatus.PENDING;

    @Column(name = "assigned_group")
    private String assignedGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel assignedUser;

    private Date completedAt;

    @Column(name = "gdpr_consent", nullable = false)
    private Boolean gdprConsent = false;

    @Column(name = "ip_address")
    private String ipAddress;
}
