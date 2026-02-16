package com.btc_store.facade.config;

import com.btc_store.domain.data.custom.DocumentData;
import com.btc_store.domain.data.custom.MenuLinkItemData;
import com.btc_store.domain.data.custom.cms.CmsCategoryData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.model.custom.CmsCategoryModel;
import com.btc_store.domain.model.custom.DocumentModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class FacadeConfig {

    protected final MediaService mediaService;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addMappings(new PropertyMap<MediaModel, MediaData>() {
            @Override
            protected void configure() {
                Converter<MediaModel, String> mediaServePathConverter =
                        ctx -> Objects.isNull(ctx.getSource()) ? StringUtils.EMPTY
                                : mediaService.generateMediaUrl(ctx.getSource().getAbsolutePath());
                using(mediaServePathConverter).map(source).setAbsolutePath(StringUtils.EMPTY);
            }
        });

        modelMapper.addMappings(new PropertyMap<CmsCategoryData, CmsCategoryModel>() {
            @Override
            protected void configure() {
                skip(destination.getCmsCategoryType());
            }
        });

        modelMapper.addMappings(new PropertyMap<UserGroupData, UserGroupModel>() {
            @Override
            protected void configure() {
                skip(destination.getUserRoles());
                skip(destination.getUsers());
            }
        });

        modelMapper.addMappings(new PropertyMap<UserData, UserModel>() {
            @Override
            protected void configure() {
                skip(destination.getUserGroups());
                skip(destination.getUserRoles());
                skip(destination.getLanguage());
            }
        });

        modelMapper.addMappings(new PropertyMap<MenuLinkItemData, MenuLinkItemModel>() {
            @Override
            protected void configure() {
                skip(destination.getUserGroups());
                skip(destination.getParentMenuLinkItem());
            }
        });

        modelMapper.addMappings(new PropertyMap<DocumentData, DocumentModel>() {
            @Override
            protected void configure() {
                skip(destination.getProducts());
            }
        });

        return modelMapper;
    }
}
