package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.ProductFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.PRODUCTS)
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    @Operation(summary = "Get all products")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ProductModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllProducts(@Parameter(description = "IsoCode for validation message internalization")
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllProducts of ProductController.");
        var products = productFacade.getAllProducts();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(products);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get product by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ProductModel', @authorizationConstants.READ))")
    public ServiceResponseData getProductByCode(@Parameter(description = "Product Code") @PathVariable String code,
                                                @Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getProductByCode of ProductController with code: {}", code);
        var product = productFacade.getProductByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(product);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update product")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ProductModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveProduct(@Parameter(description = "Product data to save")
                                           @Validated @RequestPart(value = "productData") ProductData productData,
                                           @Parameter(description = "Main image file")
                                           @RequestPart(value = "mainImageFile", required = false) MultipartFile mainImageFile,
                                           @Parameter(description = "Additional image files")
                                           @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                           @Parameter(description = "Remove existing main image")
                                           @RequestParam(value = "removeMainImage", required = false, defaultValue = "false") String removeMainImage,
                                           @Parameter(description = "IsoCode for validation message internalization")
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside saveProduct of ProductController.");
        boolean shouldRemoveMainImage = "true".equals(removeMainImage);
        var savedProduct = productFacade.saveProduct(productData, mainImageFile, imageFiles, shouldRemoveMainImage);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedProduct);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete product by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ProductModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteProduct(@Parameter(description = "Product Code") @PathVariable String code,
                                             @Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteProduct of ProductController with code: {}", code);
        productFacade.deleteProduct(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE + "/documents")
    @Operation(summary = "Get documents for a product - requires authentication")
    @PreAuthorize("isAuthenticated()")
    public ServiceResponseData getProductDocuments(@Parameter(description = "Product Code") @PathVariable String code,
                                                   @Parameter(description = "IsoCode for validation message internalization")
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getProductDocuments of ProductController with code: {}", code);
        var documents = productFacade.getProductDocuments(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(documents);
        return responseData;
    }
}
