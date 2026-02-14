package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.media.BinaryMediaData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.media.MediaRelationData;
import com.btc_store.domain.data.custom.media.ResponseMediaData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.media.MediaFacade;
import com.btc_store.facade.pageable.PageableProvider;
import com.btc_store.service.*;
import com.btc_store.service.constant.ServiceConstant;
import com.btc_store.service.exception.media.MediaCreateException;
import com.btc_store.service.exception.media.MediaDeleteException;
import com.btc_store.service.exception.media.MediaStorageException;
import constant.PackageConstant;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import util.StoreClassUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaFacadeImpl implements MediaFacade {

    protected final MediaService mediaService;
    protected final CmsCategoryService cmsCategoryService;
    protected final SiteService siteService;
    protected final SearchService searchService;
    protected final ModelMapper modelMapper;
    protected final ModelService modelService;
    protected final PageableProvider pageableProvider;

    @Value("${media.folder.serve.path}")
    private String mediaServePath;

    @Override
    public BinaryMediaData getBinaryMediaByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var mediaModel = mediaService.getMediaByCode(code, siteModel);
        var binaryMediaData = new BinaryMediaData();
        modelMapper.map(mediaModel, binaryMediaData);
        binaryMediaData.setBinary(mediaService.getMediaFileAsBinary(mediaModel));
        return binaryMediaData;
    }

    @Override
    public PageableData getMediasByCategory(Pageable pageable, String cmsCategoryCode) {
        var siteModel = siteService.getCurrentSite();
        var cmsCmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(cmsCategoryCode, siteModel);
        var mediaModels = mediaService.getMediasByCategory(pageable, cmsCmsCategoryModel, siteModel);
        return pageableProvider.map(mediaModels, MediaData.class);
    }

    @Override
    public ResponseMediaData uploadMedia(MultipartFile file, String cmsCategoryCode) throws MediaStorageException {
        var siteModel = siteService.getCurrentSite();
        var cmsCmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(cmsCategoryCode, siteModel);
        var mediaModel = mediaService.storage(file, Boolean.TRUE, cmsCmsCategoryModel, siteModel);
        ResponseMediaData responseMediaData = new ResponseMediaData();
        responseMediaData.setFileName(mediaModel.getRealFileName());
        responseMediaData.setUrl(mediaServePath + mediaModel.getAbsolutePath());
        responseMediaData.setUploaded(1);
        return responseMediaData;
    }

    @Override
    public void createMedias(List<MultipartFile> files, String itemType, String fieldName, String itemCode, String cmsCategoryCode) {

        var className = StoreClassUtils.generateClassName(itemType, ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX);
        var itemModel = searchService.searchSingleResult(StoreClassUtils.getClassForPackage(className, PackageConstant.DOMAIN_PACKAGE),
                Map.of(StringUtils.equals(className, StoreClassUtils.getSimpleName(UserModel.class)) ?
                                UserModel.Fields.username : StoreCodeBasedItemModel.Fields.code, itemCode,
                        StoreSiteBasedItemModel.Fields.site, siteService.getCurrentSite()), SearchOperator.AND);
        var siteModel = siteService.getCurrentSite();
        var cmsCmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(cmsCategoryCode, siteModel);
        try {
            var fieldType = PropertyUtils.getPropertyType(itemModel, fieldName);
            if (StringUtils.startsWith(fieldType.getName(), "java.util")) {
                var medias = findJavaType(fieldType.getTypeName());
                files.forEach(f -> medias.add(mediaService.storage(f, true, cmsCmsCategoryModel, siteModel)));
                var currentMedias = (Collection<MediaModel>) PropertyUtils.getSimpleProperty(itemModel, fieldName);
                medias.addAll(currentMedias);
                PropertyUtils.setSimpleProperty(itemModel, fieldName, medias);

            } else {
                if (CollectionUtils.size(files) > 1) {
                    log.error("Files count can not be greater than 1");
                    throw new MediaCreateException("Files count can not be greater than 1");
                }

                var currentMedia = (MediaModel) PropertyUtils.getSimpleProperty(itemModel, fieldName);

                var mediaModel = mediaService.storage(files.get(0), true, cmsCmsCategoryModel, siteModel);
                PropertyUtils.setSimpleProperty(itemModel, fieldName, mediaModel);

                if (Objects.nonNull(currentMedia)) {
                    mediaService.flagMediaForDelete(currentMedia.getCode(), siteModel);
                }

            }
            modelService.save(itemModel);
        } catch (Exception e) {
            log.error("Error occurred while create/storage media process ", ExceptionUtils.getMessage(e));
            throw new MediaStorageException("Error occurred while create/storage media process " + e.getMessage());
        }
    }

    @Override
    public void removeMedia(String itemType, String fieldName, String itemCode, String code) {
        var className = StoreClassUtils.generateClassName(itemType, ServiceConstant.HYPHEN, PackageConstant.MODEL_PREFIX);
        var itemModel = searchService.searchSingleResult(StoreClassUtils.getClassForPackage(className, PackageConstant.DOMAIN_PACKAGE),
                Map.of(StringUtils.equals(className, StoreClassUtils.getSimpleName(UserModel.class)) ?
                                UserModel.Fields.username : StoreCodeBasedItemModel.Fields.code, itemCode,
                        StoreSiteBasedItemModel.Fields.site, siteService.getCurrentSite()), SearchOperator.AND);

        try {
            var fieldType = PropertyUtils.getPropertyType(itemModel, fieldName);
            if (StringUtils.startsWith(fieldType.getName(), "java.util")) {
                var currentMedias = (Collection<MediaModel>) PropertyUtils.getSimpleProperty(itemModel, fieldName);
                currentMedias.removeIf(m -> StringUtils.equalsIgnoreCase(m.getCode(), code));
                PropertyUtils.setSimpleProperty(itemModel, fieldName, currentMedias);

            } else {
                PropertyUtils.setSimpleProperty(itemModel, fieldName, null);
            }
            modelService.save(itemModel);

            mediaService.flagMediaForDelete(code, siteService.getCurrentSite());

        } catch (Exception e) {
            log.error("Error occurred while deleting media ", ExceptionUtils.getMessage(e));
            throw new MediaDeleteException("Error occurred while deleting media " + e.getMessage());
        }
    }

    @Override
    @SneakyThrows
    public List<MediaData> getMediasByRelation(MediaRelationData mediaRelationData) {
        var siteModel = siteService.getCurrentSite();
        var itemModel = getGenericType(mediaRelationData, siteModel);
        var propertyDescriptor = PropertyUtils.getPropertyDescriptor(itemModel, mediaRelationData.getRelationField());
        var returnType = PropertyUtils.getReadMethod(propertyDescriptor).getReturnType();
        var relationMediaModels = findJavaType(returnType.getTypeName());
        if (StringUtils.equals(returnType.getSimpleName(), "Set") || StringUtils.equals(returnType.getSimpleName(), "List")) {

            var alreadyMedias = ((Collection<MediaModel>) PropertyUtils.getSimpleProperty(itemModel, mediaRelationData.getRelationField()));
            relationMediaModels = CollectionUtils.isEmpty(alreadyMedias) ? relationMediaModels : alreadyMedias;

        } else {
            var mediaModel = (MediaModel) PropertyUtils.getSimpleProperty(itemModel, mediaRelationData.getRelationField());
            if (Objects.nonNull(mediaModel)) {
                relationMediaModels.add(mediaModel);
            }
        }
        return List.of(modelMapper.map(relationMediaModels, MediaData[].class));
    }

    @SneakyThrows
    private ItemModel getGenericType(MediaRelationData mediaRelationData, SiteModel siteModel) {

        if (Objects.isNull(mediaRelationData)) {
            throw new IllegalArgumentException("Medias can not be null");
        }

        //find item...
        Class<? extends ItemModel> itemClass = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(
                mediaRelationData.getField(), ServiceConstant.HYPHEN,
                PackageConstant.MODEL_PREFIX), PackageConstant.DOMAIN_PACKAGE);

        ItemModel itemModel;
        if (mediaRelationData.isNew()) {
            itemModel = modelService.create(itemClass);
            PropertyUtils.setProperty(itemModel, SiteBasedItemModel.Fields.site, siteModel);
            PropertyUtils.setProperty(itemModel, CodeBasedItemModel.Fields.code, UUID.randomUUID().toString());

            //if parent is exist
            if (Objects.nonNull(mediaRelationData.getParent()) && StringUtils.isNotEmpty(mediaRelationData.getParent().getField())) {
                Class<? extends ItemModel> parentClass = StoreClassUtils.getClassForPackage(StoreClassUtils.generateClassName(
                        mediaRelationData.getParent().getField(), ServiceConstant.HYPHEN,
                        PackageConstant.MODEL_PREFIX), PackageConstant.DOMAIN_PACKAGE);

                var itemSearchMap = new HashMap<>();
                itemSearchMap.put(CodeBasedItemModel.Fields.code, mediaRelationData.getParent().getCode());
                itemSearchMap.put(SiteBasedItemModel.Fields.site, siteModel);

                var parentModel = searchService.searchSingleResult(parentClass, itemSearchMap, SearchOperator.AND);
                PropertyUtils.setProperty(itemModel, mediaRelationData.getParent().getRelationField(), parentModel);
            }

            modelService.save(itemModel);
        } else {
            var itemSearchMap = new HashMap<>();
            itemSearchMap.put(CodeBasedItemModel.Fields.code, mediaRelationData.getCode());
            itemSearchMap.put(SiteBasedItemModel.Fields.site, siteModel);
            itemModel = searchService.searchSingleResult(itemClass, itemSearchMap, SearchOperator.AND);
        }
        return itemModel;
    }

    private Collection<MediaModel> findJavaType(String typeName) {
        if (StringUtils.startsWith(typeName, "java.util.Set")) {
            return new HashSet<>();
        } else {
            return new ArrayList<>();
        }
    }
}
