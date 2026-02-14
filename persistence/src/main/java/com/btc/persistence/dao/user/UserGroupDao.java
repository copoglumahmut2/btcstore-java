package com.btc.persistence.dao.user;

import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.user.UserGroupModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserGroupDao extends JpaRepository<UserGroupModel, Long> {

    UserGroupModel getByCodeAndSite(String code, SiteModel site);

    Set<UserGroupModel> getBySite(SiteModel site);

    Set<UserGroupModel> getUserGroupModelsByCodeInAndSite(Set<String> codes, SiteModel site);
}
