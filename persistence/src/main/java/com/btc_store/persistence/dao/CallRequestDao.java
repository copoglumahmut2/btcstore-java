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
    
    List<CallRequestModel> findByAssignedGroupAndStatus(String assignedGroup, CallRequestStatus status);
    
    @Query("SELECT cr FROM CallRequestModel cr WHERE cr.assignedUser.id = :userId AND cr.status = :status")
    List<CallRequestModel> findByAssignedUserIdAndStatus(@Param("userId") Long userId, @Param("status") CallRequestStatus status);
    
    @Query("SELECT cr FROM CallRequestModel cr WHERE cr.site = :site ORDER BY cr.createdDate DESC")
    List<CallRequestModel> findBySiteOrderByCreatedDateDesc(@Param("site") SiteModel site);
}
