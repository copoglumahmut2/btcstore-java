package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.DocumentData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.CmsCategoryModel;
import com.btc_store.domain.model.custom.DocumentModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.ProductModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.DocumentFacade;
import com.btc_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentFacadeImpl implements DocumentFacade {

    private final SiteService siteService;
    private final ModelService modelService;
    private final ModelMapper modelMapper;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<DocumentData> getAllDocuments() {
        var siteModel = siteService.getCurrentSite();
        var documentModels = searchService.search(DocumentModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(documentModels, DocumentData[].class));
    }

    @Override
    public DocumentData getDocumentByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var documentModel = searchService.searchByCodeAndSite(DocumentModel.class, code, siteModel);
        return modelMapper.map(documentModel, DocumentData.class);
    }

    @Override
    public DocumentData saveDocument(DocumentData documentData, List<MultipartFile> mediaFiles) {
        DocumentModel documentModel;
        var siteModel = siteService.getCurrentSite();
        List<MediaModel> existingMediaFiles = new ArrayList<>();

        if (documentData.isNew()) {
            documentModel = modelMapper.map(documentData, DocumentModel.class);
            documentModel.setCode(UUID.randomUUID().toString());
            documentModel.setSite(siteModel);
        } else {
            documentModel = searchService.searchByCodeAndSite(DocumentModel.class, documentData.getCode(), siteModel);
            existingMediaFiles = new ArrayList<>(documentModel.getMedias());
            modelMapper.map(documentData, documentModel);
        }

        List<ProductModel> products = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(documentData.getProducts())) {
            for (var product : documentData.getProducts()) {
                products.add(searchService.searchByCodeAndSite(ProductModel.class, product.getCode(), siteModel));
            }
        }
        documentModel.setProducts(products);

        handleDocumentMediaFiles(documentData, documentModel, mediaFiles, existingMediaFiles, siteModel);

        var savedModel = modelService.save(documentModel);
        return modelMapper.map(savedModel, DocumentData.class);
    }

    @Override
    public void deleteDocument(String code) {
        var siteModel = siteService.getCurrentSite();
        var documentModel = searchService.searchByCodeAndSite(DocumentModel.class, code, siteModel);
        documentModel.setDeleted(true);
        documentModel.setActive(false);
        modelService.save(documentModel);
    }

    private void handleDocumentMediaFiles(DocumentData documentData, DocumentModel documentModel,
                                          List<MultipartFile> mediaFiles, List<MediaModel> existingMediaFiles,
                                          SiteModel siteModel) {
        if (Objects.nonNull(mediaFiles) && !mediaFiles.isEmpty()) {
            uploadAndSetNewMediaFiles(documentData, documentModel, mediaFiles, existingMediaFiles, siteModel);
        } else {
            updateExistingMediaFiles(documentData, documentModel, existingMediaFiles);
        }
    }

    private void uploadAndSetNewMediaFiles(DocumentData documentData, DocumentModel documentModel,
                                           List<MultipartFile> mediaFiles, List<MediaModel> existingMediaFiles,
                                           SiteModel siteModel) {
        try {
            var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
            List<MediaModel> finalMediaFiles = new ArrayList<>();

            addExistingMediaFilesInOrder(documentData, existingMediaFiles, finalMediaFiles);
            uploadNewMediaFiles(mediaFiles, cmsCategoryModel, siteModel, finalMediaFiles);

            documentModel.setMedias(finalMediaFiles);
            log.info("Total {} media files processed", finalMediaFiles.size());
        } catch (Exception e) {
            log.error("Error uploading document media files: {}", e.getMessage());
            throw new RuntimeException("Error uploading document media files: " + e.getMessage());
        }
    }

    private void addExistingMediaFilesInOrder(DocumentData documentData, List<MediaModel> existingMediaFiles,
                                              List<MediaModel> finalMediaFiles) {
        if (CollectionUtils.isNotEmpty(documentData.getMediaFileCodesInOrder())) {
            for (String mediaCode : documentData.getMediaFileCodesInOrder()) {
                existingMediaFiles.stream()
                        .filter(media -> media.getCode().equals(mediaCode))
                        .findFirst()
                        .ifPresent(finalMediaFiles::add);
            }
            log.info("{} existing media files preserved", finalMediaFiles.size());
        }
    }

    private void uploadNewMediaFiles(List<MultipartFile> mediaFiles, CmsCategoryModel cmsCategoryModel,
                                     SiteModel siteModel, List<MediaModel> finalMediaFiles) {
        for (MultipartFile mediaFile : mediaFiles) {
            if (!mediaFile.isEmpty()) {
                var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                finalMediaFiles.add(mediaModel);
            }
        }
        log.info("{} new media files uploaded", mediaFiles.size());
    }

    private void updateExistingMediaFiles(DocumentData documentData, DocumentModel documentModel,
                                          List<MediaModel> existingMediaFiles) {
        if (Objects.nonNull(documentData.getMediaFileCodesInOrder())) {
            List<MediaModel> orderedMediaFiles = new ArrayList<>();
            for (String mediaCode : documentData.getMediaFileCodesInOrder()) {
                existingMediaFiles.stream()
                        .filter(media -> media.getCode().equals(mediaCode))
                        .findFirst()
                        .ifPresent(orderedMediaFiles::add);
            }
            documentModel.setMediaFiles(orderedMediaFiles);
            log.info("Media files order updated. New: {}, Original: {}", orderedMediaFiles.size(), existingMediaFiles.size());
        } else {
            documentModel.setMediaFiles(existingMediaFiles);
        }
    }
}
