package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.SuccessStoryData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.SuccessStoryFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.SUCCESS_STORIES)
public class SuccessStoryController {

    private final SuccessStoryFacade successStoryFacade;

    @GetMapping
    @Operation(summary = "Get all success stories ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SuccessStoryModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllSuccessStories(@Parameter(description = "IsoCode for validation message internalization") 
                                                    @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllSuccessStories of SuccessStoryController.");
        var successStories = successStoryFacade.getAllSuccessStories();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(successStories);
        return responseData;
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active success stories ordered by display order")
    public ServiceResponseData getActiveSuccessStories(@Parameter(description = "IsoCode for validation message internalization") 
                                                       @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveSuccessStories of SuccessStoryController.");
        var successStories = successStoryFacade.getActiveSuccessStories();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(successStories);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get success story by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SuccessStoryModel', @authorizationConstants.READ))")
    public ServiceResponseData getSuccessStoryByCode(@Parameter(description = "Success Story Code") @PathVariable String code,
                                                     @Parameter(description = "IsoCode for validation message internalization") 
                                                     @RequestParam(required = false) String isoCode) {
        log.info("Inside getSuccessStoryByCode of SuccessStoryController with code: {}", code);
        var successStory = successStoryFacade.getSuccessStoryByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(successStory);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update success story")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SuccessStoryModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveSuccessStory(@Parameter(description = "Success story data to save") 
                                                @Validated @RequestPart(value = "successStoryData") SuccessStoryData successStoryData,
                                                @Parameter(description = "Success story media file") 
                                                @RequestPart(value = "media", required = false) MultipartFile mediaFile,
                                                @Parameter(description = "Remove existing media") 
                                                @RequestPart(value = "removeMedia", required = false) String removeMedia,
                                                @Parameter(description = "IsoCode for validation message internalization") 
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside saveSuccessStory of SuccessStoryController.");
        boolean shouldRemoveMedia = "true".equals(removeMedia);
        var savedSuccessStory = successStoryFacade.saveSuccessStory(successStoryData, mediaFile, shouldRemoveMedia);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedSuccessStory);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete success story by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SuccessStoryModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteSuccessStory(@Parameter(description = "Success Story Code") @PathVariable String code,
                                                  @Parameter(description = "IsoCode for validation message internalization") 
                                                  @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteSuccessStory of SuccessStoryController with code: {}", code);
        successStoryFacade.deleteSuccessStory(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
