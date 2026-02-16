package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.UserFacade;
import com.btc_store.service.*;
import com.btc_store.service.user.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacadeImpl implements UserFacade {

    private final SiteService siteService;
    private final ModelService modelService;
    private final ModelMapper modelMapper;
    private final SearchService searchService;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupService userGroupService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<UserData> getAllUsers() {
        var siteModel = siteService.getCurrentSite();
        var userModels = searchService.search(UserModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(userModels, UserData[].class));
    }

    @Override
    public UserData getUserByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var userModel = searchService.searchByCodeAndSite(UserModel.class, code, siteModel);
        return modelMapper.map(userModel, UserData.class);
    }

    @Override
    public UserData saveUser(UserData userData, MultipartFile pictureFile, boolean removePicture) {
        UserModel userModel;
        var siteModel = siteService.getCurrentSite();
        MediaModel oldPicture = null;

        if (userData.isNew()) {
            userModel = modelMapper.map(userData, UserModel.class);
            userModel.setCode(UUID.randomUUID().toString());
            userModel.setSite(siteModel);
        } else {
            userModel = searchService.searchByCodeAndSite(UserModel.class, userData.getCode(), siteModel);
            oldPicture = userModel.getPicture();
            MediaModel pictureToKeep = userModel.getPicture();
            modelMapper.map(userData, userModel);

            if ((Objects.isNull(pictureFile) || pictureFile.isEmpty()) && !removePicture) {
                userModel.setPicture(pictureToKeep);
            }
        }


        Set<UserGroupModel> userGroups = new HashSet<>();
        if (CollectionUtils.isNotEmpty(userData.getUserGroups())) {
            for (var userGroup : userData.getUserGroups()) {
                userGroups.add(userGroupService.getUserGroupModel(userGroup.getCode(), siteModel));
            }
        }
        userModel.setUserGroups(userGroups);

        if (StringUtils.isNotEmpty(userData.getDefinedPassword())) {
            userModel.setPassword(passwordEncoder.encode(userData.getDefinedPassword()));
            userModel.setPasswordEncoded(Boolean.TRUE);
        } else {
            userModel.setPasswordEncoded(Boolean.TRUE);
        }

        // Handle profile picture upload
        boolean hasNewPicture = Objects.nonNull(pictureFile) && !pictureFile.isEmpty();

        if (hasNewPicture) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.USER.getValue(), siteModel);
                var mediaModel = mediaService.storage(pictureFile, false, cmsCategoryModel, siteModel);
                userModel.setPicture(mediaModel);

                if (Objects.nonNull(oldPicture)) {
                    mediaService.flagMediaForDelete(oldPicture.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing user profile picture: {}", e.getMessage());
                throw new RuntimeException("Error storing user profile picture: " + e.getMessage());
            }
        } else if (removePicture && Objects.nonNull(oldPicture)) {
            // User explicitly wants to remove the picture
            try {
                mediaService.flagMediaForDelete(oldPicture.getCode(), siteModel);
                userModel.setPicture(null);
            } catch (Exception e) {
                log.error("Error removing user profile picture: {}", e.getMessage());
            }
        }

        var savedModel = modelService.save(userModel);
        return modelMapper.map(savedModel, UserData.class);
    }

    @Override
    public void deleteUser(String code) {
        var siteModel = siteService.getCurrentSite();
        var userModel = searchService.searchByCodeAndSite(UserModel.class, code, siteModel);
        userModel.setDeleted(true);
        userModel.setActive(false);
        modelService.save(userModel);
    }
}
