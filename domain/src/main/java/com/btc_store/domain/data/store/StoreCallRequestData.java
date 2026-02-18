package com.btc_store.domain.data.store;

import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.CallRequestStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreCallRequestData extends BackBaseData {
    
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String subject;
    private String message;
    private CallRequestStatus status;
    private String assignedGroup;
    private Long assignedUserId;
    private String assignedUserName;
    private Date completedAt;
    private Boolean gdprConsent;
    private String ipAddress;
}
