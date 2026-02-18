package com.btc_store.domain.data.store;

import com.btc_store.domain.data.extend.back.BackBaseData;
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
    private CallRequestStatus status;
    
    // Multi-assign fields
    private List<AssignedGroupInfo> assignedGroupsList; // List of assigned groups with details
    private List<AssignedUserInfo> assignedUsersList; // List of assigned users with details
    
    // Simple string representations for backward compatibility
    private String assignedGroups; // Semicolon separated group codes
    private List<String> assignedUserNames; // List of assigned user names
    
    // Deprecated fields (for backward compatibility)
    @Deprecated
    private String assignedGroup;
    @Deprecated
    private Long assignedUserId;
    @Deprecated
    private String assignedUserName;
    
    private Date completedAt;
    private Boolean gdprConsent;
    private String ipAddress;
    
    // Inner classes for detailed info
    @Data
    public static class AssignedGroupInfo {
        private String code;
        private String name; // Default name (usually Turkish)
        private LocalizedDescription description; // All language descriptions
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
