package com.btc.facade.media;

import com.btc.domain.data.custom.media.*;
import com.btc.domain.data.custom.pageable.PageableData;
import com.btc.service.exception.media.MediaStorageException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface MediaFacade {

    BinaryMediaData getBinaryMediaByCode(String code);

    PageableData getMediasByCategory(Pageable pageable, String cmsCategoryCode);

    ResponseMediaData uploadMedia(MultipartFile file, String cmsCategoryCode) throws MediaStorageException;

    void createMedias(List<MultipartFile> files, String itemType, String fieldName, String itemCode, String cmsCategoryCode);

    void removeMedia(String itemType, String fieldName, String itemCode, String code);

    List<MediaData> getMediasByRelation(MediaRelationData mediaRelationData);
}
