package com.btc_store.facade;

import com.btc_store.domain.data.custom.PartnerData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PartnerFacade {

    List<PartnerData> getAllPartners();

    List<PartnerData> getActivePartners();

    List<PartnerData> getHomePagePartners();

    PartnerData getPartnerByCode(String code);

    PartnerData savePartner(PartnerData partnerData, MultipartFile mediaFile, boolean removeMedia);

    void deletePartner(String code);
}
