package com.btc.persistence.dao;

import com.btc.domain.model.custom.extend.ItemModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Transactional
@Service
public interface ModelDao extends JpaRepository<ItemModel, Long> {
}
