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

        if (documentData.isNew()) {
            documentModel = modelMapper.map(documentData, DocumentModel.class);
            documentModel.setCode(UUID.randomUUID().toString());
            documentModel.setSite(siteModel);
        } else {
            documentModel = searchService.searchByCodeAndSite(DocumentModel.class, documentData.getCode(), siteModel);
            modelMapper.map(documentData, documentModel);
        }

        List<ProductModel> products = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(documentData.getProducts())) {
            for (var product : documentData.getProducts()) {
                products.add(searchService.searchByCodeAndSite(ProductModel.class, product.getCode(), siteModel));
            }
        }
        documentModel.setProducts(products);
        
        if (Objects.nonNull(documentData.getMedias())) {
            List<MediaModel> existingMedias = new ArrayList<>();
            for (var mediaData : documentData.getMedias()) {
                if (Objects.nonNull(mediaData.getCode())) {
                    var mediaModel = searchService.searchByCodeAndSite(MediaModel.class, mediaData.getCode(), siteModel);
                    if (Objects.nonNull(mediaModel)) {
                        existingMedias.add(mediaModel);
                    }
                }
            }
            documentModel.setMedias(existingMedias);
            log.info("Updated document with {} existing media files", existingMedias.size());
        }

        // Yeni media dosyalarını yükle ve ekle
        if (Objects.nonNull(mediaFiles) && !mediaFiles.isEmpty()) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
                List<MediaModel> uploadedMediaFiles = new ArrayList<>();
                
                for (MultipartFile mediaFile : mediaFiles) {
                    if (!mediaFile.isEmpty()) {
                        var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                        uploadedMediaFiles.add(mediaModel);
                    }
                }
                
                // Mevcut dosyalara yeni dosyaları ekle
                if (Objects.isNull(documentModel.getMedias())) {
                    documentModel.setMedias(uploadedMediaFiles);
                } else {
                    documentModel.getMedias().addAll(uploadedMediaFiles);
                }
                
                log.info("Uploaded {} new media files for document", uploadedMediaFiles.size());
            } catch (Exception e) {
                log.error("Error uploading document media files: {}", e.getMessage());
                throw new RuntimeException("Error uploading document media files: " + e.getMessage());
            }
        }

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
}
