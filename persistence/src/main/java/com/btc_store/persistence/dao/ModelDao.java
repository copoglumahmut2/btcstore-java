package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.extend.ItemModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Transactional
@Service
public interface ModelDao extends JpaRepository<ItemModel, Long> {
}
