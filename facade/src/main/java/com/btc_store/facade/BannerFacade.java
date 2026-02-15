package com.btc_store.facade;

import com.btc_store.domain.data.custom.BannerData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BannerFacade {

    List<BannerData> getAllBanners();

    List<BannerData> getActiveBanners();

    BannerData getBannerByCode(String code);

    BannerData saveBanner(BannerData bannerData, MultipartFile mediaFile, boolean removeMedia);

    void deleteBanner(String code);
}
