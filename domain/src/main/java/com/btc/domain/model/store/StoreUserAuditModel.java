package com.btc.domain.model.store;

import com.btc.domain.constant.DomainConstant;
import com.btc.domain.enums.LoginResultType;
import com.btc.domain.model.custom.extend.CodeBasedItemModel;
import com.btc.domain.model.custom.user.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import java.io.Serial;
import java.util.Date;

@Entity
@Table(name = DomainConstant.USER_AUDIT_TABLE_NAME)
@Getter
@Setter
@FieldNameConstants
public class StoreUserAuditModel extends CodeBasedItemModel {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final String USER_AUDIT_RELATION = "useraudittable_id";

    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel user;

    private Date loginDate;

    @Enumerated(EnumType.STRING)
    private LoginResultType loginResult;

    private String ip;

    private String clientInfo;

}
