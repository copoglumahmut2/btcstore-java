package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.CallRequestHistoryModel;
import com.btc_store.domain.model.custom.CallRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallRequestHistoryDao extends JpaRepository<CallRequestHistoryModel, Long> {
    
    @Query("SELECT crh FROM CallRequestHistoryModel crh WHERE crh.callRequest = :callRequest ORDER BY crh.createdDate DESC")
    List<CallRequestHistoryModel> findByCallRequestOrderByCreatedDateDesc(@Param("callRequest") CallRequestModel callRequest);
    
    @Query("SELECT crh FROM CallRequestHistoryModel crh WHERE crh.callRequest.id = :callRequestId ORDER BY crh.createdDate DESC")
    List<CallRequestHistoryModel> findByCallRequestIdOrderByCreatedDateDesc(@Param("callRequestId") Long callRequestId);
}
