package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.CategoryFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.CATEGORIES)
public class CategoryController {

    private final CategoryFacade categoryFacade;

    @GetMapping
    @Operation(summary = "Get all categories ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CategoryModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllCategories(@Parameter(description = "IsoCode for validation message internalization") 
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllCategories of CategoryController.");
        var categories = categoryFacade.getAllCategories();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(categories);
        return responseData;
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active categories ordered by display order")
    public ServiceResponseData getActiveCategories(@Parameter(description = "IsoCode for validation message internalization") 
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveCategories of CategoryController.");
        var categories = categoryFacade.getActiveCategories();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(categories);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get category by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CategoryModel', @authorizationConstants.READ))")
    public ServiceResponseData getCategoryByCode(@Parameter(description = "Category Code") @PathVariable String code,
                                                 @Parameter(description = "IsoCode for validation message internalization") 
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside getCategoryByCode of CategoryController with code: {}", code);
        var category = categoryFacade.getCategoryByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(category);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update category")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CategoryModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveCategory(@Parameter(description = "Category data to save") 
                                            @Validated @RequestPart(value = "categoryData") CategoryData categoryData,
                                            @Parameter(description = "Category media file") 
                                            @RequestPart(value = "media", required = false) MultipartFile mediaFile,
                                            @Parameter(description = "Remove existing media") 
                                            @RequestPart(value = "removeMedia", required = false) String removeMedia,
                                            @Parameter(description = "IsoCode for validation message internalization") 
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside saveCategory of CategoryController.");
        boolean shouldRemoveMedia = "true".equals(removeMedia);
        var savedCategory = categoryFacade.saveCategory(categoryData, mediaFile, shouldRemoveMedia);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedCategory);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete category by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CategoryModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteCategory(@Parameter(description = "Category Code") @PathVariable String code,
                                              @Parameter(description = "IsoCode for validation message internalization") 
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteCategory of CategoryController with code: {}", code);
        categoryFacade.deleteCategory(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
