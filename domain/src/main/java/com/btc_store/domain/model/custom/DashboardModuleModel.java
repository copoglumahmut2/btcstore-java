package com.btc_store.domain.model.custom;

import com.btc_store.domain.model.store.StoreDashboardModuleModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class DashboardModuleModel extends StoreDashboardModuleModel {

}
