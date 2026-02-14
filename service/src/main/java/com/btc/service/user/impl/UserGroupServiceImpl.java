package com.btc.service.user.impl;

import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.user.UserGroupModel;
import com.btc.persistence.dao.user.UserGroupDao;
import com.btc.service.user.UserGroupService;
import com.btc.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * UserGroup Service Class
 *
 * @author mcatal
 * @version v1.0
 * @since 01.11.2021
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupDao userGroupDao;

    @Override
    public UserGroupModel getUserGroupModel(String code, SiteModel siteModel) {
        var userGroupModel = userGroupDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(userGroupModel, UserGroupModel.class, siteModel, code);
        return userGroupModel;
    }

    @Override
    public UserGroupModel getUserGroupModelForBack(String code, SiteModel siteModel) {
        return userGroupDao.getByCodeAndSite(code, siteModel);
    }

    @Override
    public Set<UserGroupModel> getUserGroupModels(SiteModel siteModel) {
        return userGroupDao.getBySite(siteModel);
    }

    @Override
    public Set<UserGroupModel> getUserGroupModelsByCodeIn(Set<String> codes, SiteModel siteModel) {
        return userGroupDao.getUserGroupModelsByCodeInAndSite(codes, siteModel);
    }
}
