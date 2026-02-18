package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.LegalDocumentData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

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

    private List<AssignedGroupInfo> assignedGroupsList;
    private List<AssignedUserInfo> assignedUsersList;

    private String assignedGroups;
    private List<String> assignedUserNames;
    private Date completedAt;
    private String ipAddress;

    private LegalDocumentData acceptedLegalDocument;

    @Data
    public static class AssignedGroupInfo {
        private String code;
        private String name;
        private LocalizedDescription description;
    }
    
    @Data
    public static class AssignedUserInfo {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
    }
    
    @Data
    public static class LocalizedDescription {
        private String tr;
        private String en;
        private String de;
        private String fr;
        private String es;
        private String it;
    }
}
