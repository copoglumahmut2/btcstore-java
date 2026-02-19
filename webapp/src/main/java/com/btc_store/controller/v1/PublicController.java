package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.BannerFacade;
import com.btc_store.facade.CallRequestFacade;
import com.btc_store.facade.CategoryFacade;
import com.btc_store.facade.LegalDocumentFacade;
import com.btc_store.facade.MenuLinkItemFacade;
import com.btc_store.facade.PartnerFacade;
import com.btc_store.facade.ProductFacade;
import com.btc_store.facade.ReferenceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
    private final com.btc_store.facade.SiteConfigurationFacade siteConfigurationFacade;
    private final CallRequestFacade callRequestFacade;
    private final LegalDocumentFacade legalDocumentFacade;
    private final ProductFacade productFacade;

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

    @GetMapping("/site-configuration")
    @Operation(summary = "Get site configuration for public display")
    public ServiceResponseData getSiteConfiguration(@Parameter(description = "IsoCode for validation message internalization") 
                                                    @RequestParam(required = false) String isoCode) {
        log.info("Inside getSiteConfiguration of PublicController.");
        var config = siteConfigurationFacade.getSiteConfiguration();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(config);
        return responseData;
    }
    
    @PostMapping("/call-requests")
    @Operation(summary = "Create call request (Public - No authentication required)")
    public ServiceResponseData createCallRequest(@Parameter(description = "Call request data to create")
                                                 @Validated @RequestBody CallRequestData callRequestData,
                                                 @Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside createCallRequest of PublicController.");
        var savedCallRequest = callRequestFacade.createCallRequest(callRequestData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedCallRequest);
        return responseData;
    }
    
    @GetMapping("/legal-documents/privacy-policy/current")
    @Operation(summary = "Get current Privacy Policy document for call request form (Public)")
    public ServiceResponseData getCurrentPrivacyPolicyDocument(@Parameter(description = "IsoCode for validation message internalization")
                                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside getCurrentPrivacyPolicyDocument of PublicController.");
        var privacyPolicyDocument = legalDocumentFacade.getCurrentPrivacyPolicyDocument();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(privacyPolicyDocument);
        return responseData;
    }
    
    @GetMapping("/products")
    @Operation(summary = "Get all active products with optional category filter and available categories for filtering")
    public ServiceResponseData getProducts(@Parameter(description = "Category code to filter products (optional)")
                                           @RequestParam(required = false) String category,
                                           @Parameter(description = "Page number (starts from 1)")
                                           @RequestParam(required = false, defaultValue = "1") Integer page,
                                           @Parameter(description = "Page size (default 20)")
                                           @RequestParam(required = false, defaultValue = "20") Integer size,
                                           @Parameter(description = "IsoCode for validation message internalization")
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside getProducts of PublicController with category: {}, page: {}, size: {}", category, page, size);
        var filterData = productFacade.getProductsWithFilters(category, page, size);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(filterData);
        return responseData;
    }
    
    @GetMapping("/products/{code}")
    @Operation(summary = "Get active product by code for public display")
    public ServiceResponseData getProductByCode(@Parameter(description = "Product code") @PathVariable String code,
                                                @Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getProductByCode of PublicController with code: {}", code);
        var product = productFacade.getActiveProductByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(product);
        return responseData;
    }
}
