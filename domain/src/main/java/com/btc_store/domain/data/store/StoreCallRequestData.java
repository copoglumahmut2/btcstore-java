package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.LegalDocumentData;
import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.data.custom.user.UserSummaryData;
import com.btc_store.domain.data.extend.BaseLocalizedDescriptionData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreCallRequestData extends BackBaseData {
    
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String subject;
    private String message;
    private CallRequestPriority priority;
    private CallRequestStatus status;
    private Set<BaseLocalizedDescriptionData> assignedGroups;
    private Set<UserSummaryData> assignedUsers;
    private Date completedAt;
    private String ipAddress;
    private LegalDocumentData acceptedLegalDocument;
    private ProductData product;
}
