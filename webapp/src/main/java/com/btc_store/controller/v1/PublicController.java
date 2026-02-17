package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.BannerFacade;
import com.btc_store.facade.CategoryFacade;
import com.btc_store.facade.MenuLinkItemFacade;
import com.btc_store.facade.PartnerFacade;
import com.btc_store.facade.ReferenceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + "/public")
@Tag(name = "Public API", description = "Public endpoints that don't require authentication")
public class PublicController {

    private final BannerFacade bannerFacade;
    private final CategoryFacade categoryFacade;
    private final PartnerFacade partnerFacade;
    private final ReferenceFacade referenceFacade;
    private final MenuLinkItemFacade menuLinkItemFacade;

    @GetMapping("/banners")
    @Operation(summary = "Get all active banners for public display")
    public ServiceResponseData getActiveBanners(@Parameter(description = "IsoCode for validation message internalization") 
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveBanners of PublicController.");
        var banners = bannerFacade.getActiveBanners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(banners);
        return responseData;
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all active categories for public display")
    public ServiceResponseData getActiveCategories(@Parameter(description = "IsoCode for validation message internalization") 
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveCategories of PublicController.");
        var categories = categoryFacade.getActiveCategories();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(categories);
        return responseData;
    }

    @GetMapping("/partners")
    @Operation(summary = "Get all active partners for public display")
    public ServiceResponseData getActivePartners(@Parameter(description = "IsoCode for validation message internalization") 
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside getActivePartners of PublicController.");
        var partners = partnerFacade.getActivePartners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(partners);
        return responseData;
    }

    @GetMapping("/partners/home")
    @Operation(summary = "Get partners to show on home page (limited)")
    public ServiceResponseData getHomePagePartners(@Parameter(description = "IsoCode for validation message internalization") 
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getHomePagePartners of PublicController.");
        var partners = partnerFacade.getHomePagePartners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(partners);
        return responseData;
    }

    @GetMapping("/references")
    @Operation(summary = "Get all active references for public display")
    public ServiceResponseData getActiveReferences(@Parameter(description = "IsoCode for validation message internalization") 
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveReferences of PublicController.");
        var references = referenceFacade.getActiveReferences();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(references);
        return responseData;
    }

    @GetMapping("/references/home")
    @Operation(summary = "Get references to show on home page (limited)")
    public ServiceResponseData getHomePageReferences(@Parameter(description = "IsoCode for validation message internalization") 
                                                     @RequestParam(required = false) String isoCode) {
        log.info("Inside getHomePageReferences of PublicController.");
        var references = referenceFacade.getHomePageReferences();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(references);
        return responseData;
    }

    @GetMapping("/menus")
    @Operation(summary = "Get all active public menus with hierarchy")
    public ServiceResponseData getPublicMenus(@Parameter(description = "IsoCode for validation message internalization") 
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside getPublicMenus of PublicController.");
        var menus = menuLinkItemFacade.getMenusByType("PUBLIC");
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(menus);
        return responseData;
    }
}
