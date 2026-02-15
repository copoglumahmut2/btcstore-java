package com.btc_store.facade;

import com.btc_store.domain.data.custom.ReferenceData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReferenceFacade {

    List<ReferenceData> getAllReferences();

    List<ReferenceData> getActiveReferences();

    List<ReferenceData> getHomePageReferences();

    ReferenceData getReferenceByCode(String code);

    ReferenceData saveReference(ReferenceData referenceData, MultipartFile mediaFile, boolean removeMedia);

    void deleteReference(String code);
}
