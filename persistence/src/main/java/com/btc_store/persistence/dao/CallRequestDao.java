package com.btc_store.persistence.dao;

import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallRequestDao extends JpaRepository<CallRequestModel, Long> {
    
    List<CallRequestModel> findBySiteAndStatus(SiteModel site, CallRequestStatus status);
    
    List<CallRequestModel> findBySite(SiteModel site);
    
    // Query for assigned groups (using join on assignedGroups collection)
    @Query("SELECT DISTINCT cr FROM CallRequestModel cr JOIN cr.assignedGroups g WHERE g.code = :groupCode AND cr.status = :status")
    List<CallRequestModel> findByAssignedGroupAndStatus(@Param("groupCode") String groupCode, @Param("status") CallRequestStatus status);
    
    // Query for assigned users (using join on assignedUsers collection)
    @Query("SELECT DISTINCT cr FROM CallRequestModel cr JOIN cr.assignedUsers u WHERE u.id = :userId AND cr.status = :status")
    List<CallRequestModel> findByAssignedUserIdAndStatus(@Param("userId") Long userId, @Param("status") CallRequestStatus status);
    
    @Query("SELECT cr FROM CallRequestModel cr WHERE cr.site = :site ORDER BY cr.createdDate DESC")
    List<CallRequestModel> findBySiteOrderByCreatedDateDesc(@Param("site") SiteModel site);
}
