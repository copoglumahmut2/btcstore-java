package com.btc_store.service;

import com.btc_store.domain.model.custom.ReferenceModel;
import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

public interface ReferenceService {

    ReferenceModel getReferenceByCode(String code, SiteModel siteModel);

    List<ReferenceModel> getAllReferences(SiteModel siteModel);

    List<ReferenceModel> getActiveReferences(SiteModel siteModel);

    List<ReferenceModel> getAllReferencesOrdered(SiteModel siteModel);

    List<ReferenceModel> getActiveReferencesOrdered(SiteModel siteModel);

    List<ReferenceModel> getHomePageReferences(SiteModel siteModel);

    ReferenceModel saveReference(ReferenceModel referenceModel);

    void deleteReference(ReferenceModel referenceModel);
}
