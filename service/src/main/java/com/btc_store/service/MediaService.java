package com.btc_store.service;

import com.btc_store.domain.model.custom.CmsCategoryModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.service.exception.media.MediaDeleteException;
import com.btc_store.service.exception.media.MediaStorageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Set;

public interface MediaService {

    MediaModel storage(MultipartFile multipartFile, boolean secure, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException;

    MediaModel storage(String realFileName, MultipartFile multipartFile, boolean secure, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException;

    MediaModel storage(File file, boolean secure, boolean move, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException;

    MediaModel storageFromContent(String fileContent, String originalFileNameWithExtention, MimeType mimeType, boolean secure, CmsCategoryModel cmsCategoryModel, SiteModel siteModel) throws MediaStorageException;

    MediaModel getMediaByCode(String code, SiteModel siteModel);

    String getMediaFileAsBinary(MediaModel mediaModel);

    Page<MediaModel> getMediasByCategory(Pageable pageable, CmsCategoryModel cmsCategoryModel, SiteModel siteModel);

    //Used from cronjob
    Set<MediaModel> getMediasFlaggedAsDeleted(SiteModel siteModel);

    void flagMediaForDelete(String code, SiteModel siteModel) throws MediaDeleteException;

    String generateMediaUrl(String url);
}
