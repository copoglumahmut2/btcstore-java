package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.BannerData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.BannerFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.BANNERS)
public class BannerController {

    private final BannerFacade bannerFacade;

    @GetMapping
    @Operation(summary = "Get all banners ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('BannerModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllBanners(@Parameter(description = "IsoCode for validation message internalization") 
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllBanners of BannerController.");
        var banners = bannerFacade.getAllBanners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(banners);
        return responseData;
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active banners ordered by display order")
    public ServiceResponseData getActiveBanners(@Parameter(description = "IsoCode for validation message internalization") 
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveBanners of BannerController.");
        var banners = bannerFacade.getActiveBanners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(banners);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get banner by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('BannerModel', @authorizationConstants.READ))")
    public ServiceResponseData getBannerByCode(@Parameter(description = "Banner Code") @PathVariable String code,
                                               @Parameter(description = "IsoCode for validation message internalization") 
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside getBannerByCode of BannerController with code: {}", code);
        var banner = bannerFacade.getBannerByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(banner);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update banner")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('BannerModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveBanner(@Parameter(description = "Banner data to save") 
                                          @Validated @RequestPart(value = "bannerData") BannerData bannerData,
                                          @Parameter(description = "Banner media file") 
                                          @RequestPart(value = "media", required = false) MultipartFile mediaFile,
                                          @Parameter(description = "IsoCode for validation message internalization") 
                                          @RequestParam(required = false) String isoCode) {
        log.info("Inside saveBanner of BannerController.");
        var savedBanner = bannerFacade.saveBanner(bannerData, mediaFile);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedBanner);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete banner by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('BannerModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteBanner(@Parameter(description = "Banner Code") @PathVariable String code,
                                            @Parameter(description = "IsoCode for validation message internalization") 
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteBanner of BannerController with code: {}", code);
        bannerFacade.deleteBanner(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
