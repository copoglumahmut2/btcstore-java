package com.btc_store.facade.media;

import com.btc_store.domain.data.custom.media.BinaryMediaData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.media.MediaRelationData;
import com.btc_store.domain.data.custom.media.ResponseMediaData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.service.exception.media.MediaStorageException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaFacade {

    BinaryMediaData getBinaryMediaByCode(String code);

    PageableData getMediasByCategory(Pageable pageable, String cmsCategoryCode);

    ResponseMediaData uploadMedia(MultipartFile file, String cmsCategoryCode) throws MediaStorageException;

    void createMedias(List<MultipartFile> files, String itemType, String fieldName, String itemCode, String cmsCategoryCode);

    void removeMedia(String itemType, String fieldName, String itemCode, String code);

    List<MediaData> getMediasByRelation(MediaRelationData mediaRelationData);
}
