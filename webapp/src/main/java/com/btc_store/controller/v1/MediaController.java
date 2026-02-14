package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.media.MediaRelationData;
import com.btc_store.domain.data.custom.media.ResponseMediaData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.media.MediaFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.MEDIAS)
public class MediaController {

    protected final MediaFacade mediaFacade;
    protected static final String MEDIA_CAT_NOT_NULL_MSG = "mediaCategory must not be null";

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Return medias related media category code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MediaModel', @authorizationConstants.READ))")
    public ServiceResponseData getBinaryMedia(@Parameter(description = "Media Code") @PathVariable String code,
                                              @Parameter(description = "IsoCode for validation message internalization") @RequestParam(required = false) String isoCode) {
        log.info("Inside getBinaryMedia of MediaController.");
        var binaryMediaData = mediaFacade.getBinaryMediaByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(binaryMediaData);
        return responseData;
    }

    @GetMapping("/{cmsCategory}/{page}")
    @Operation(summary = "Return medias related media category code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MediaModel', @authorizationConstants.READ))")
    public ServiceResponseData getMedias(@Parameter(description = "Media Category code") @PathVariable String cmsCategory,
                                         @Parameter(description = "Pagination page number") @PathVariable int page,
                                         @Parameter(description = "IsoCode for validation message internalization") @RequestParam(required = false) String isoCode) {
        log.info("Inside getMedias of MediaController.");
        var mediaCategory = MediaCategory.valueOf(cmsCategory.toUpperCase());
        Assert.notNull(mediaCategory, MEDIA_CAT_NOT_NULL_MSG);
        var pageable = PageRequest.of(page - 1, 5);
        var medias = mediaFacade.getMediasByCategory(pageable, cmsCategory);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(medias);
        return responseData;
    }

    @PostMapping("/{cmsCategory}/upload")
    @Operation(summary = "Upload file to media server")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MediaModel', @authorizationConstants.SAVE))")
    public ResponseMediaData uploadMedia(@Parameter(description = "File which will be uploaded") @RequestPart(value = "upload") MultipartFile file,
                                         @Parameter(description = "Media Category code") @PathVariable String cmsCategory,
                                         @Parameter(description = "IsoCode for validation message internalization") @RequestParam(required = false) String isoCode) {
        log.info("Inside uploadMedia of MediaController.", file);
        Assert.notNull("file", "File must not be null");
        var mediaCategory = MediaCategory.valueOf(cmsCategory.toUpperCase());
        Assert.notNull(mediaCategory, MEDIA_CAT_NOT_NULL_MSG);
        return mediaFacade.uploadMedia(file, cmsCategory);
    }

    @PostMapping("/{cmsCategory}/{itemType}/{fieldName}/{itemCode}")
    @Operation(summary = "Upload file to media server")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MediaModel', @authorizationConstants.SAVE))")
    public ServiceResponseData createMedia(@Parameter(description = "File which will be uploaded") @RequestPart(value = "medias") List<MultipartFile> files,
                                           @Parameter(description = "Media Category code") @PathVariable String cmsCategory,
                                           @Parameter(description = "Generic Item type") @PathVariable String itemType,
                                           @Parameter(description = "Generic Item type fieldName") @PathVariable String fieldName,
                                           @Parameter(description = "Generic Item type code") @PathVariable String itemCode,
                                           @Parameter(description = "IsoCode for validation message internalization") @RequestParam(required = false) String isoCode) {
        log.info("Inside createMedia of MediaController.", files, itemType, fieldName, itemCode);
        Assert.notNull("files", "Files must not be null");
        mediaFacade.createMedias(files, itemType, fieldName, itemCode, cmsCategory);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }


    @DeleteMapping("/{itemType}/{fieldName}/{itemCode}/{code}")
    @Operation(summary = "Delete Media")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MediaModel', @authorizationConstants.DELETE))")
    public ServiceResponseData removeMedia(@Parameter(description = "Generic Item type") @PathVariable String itemType,
                                           @Parameter(description = "Generic Item type fieldName") @PathVariable String fieldName,
                                           @Parameter(description = "Generic Item type code") @PathVariable String itemCode,
                                           @Parameter(description = "Media Code") @PathVariable String code,
                                           @Parameter(description = "IsoCode for validation message internalization") @RequestParam(required = false) String isoCode) {
        log.info("Inside removeMedia of MediaController.", itemType, fieldName, itemCode, code);
        mediaFacade.removeMedia(itemType, fieldName, itemCode, code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }

    @PostMapping("/relations")
    @Operation(summary = "Get media by generic relation")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MediaModel',@authorizationConstants.READ))")
    public ServiceResponseData getMediasByRelation(@Parameter(description = "Medias by relation data")
                                                   @Validated @RequestBody MediaRelationData mediaRelationData,
                                                   @Parameter(description = "IsoCode for validation message internalization.")
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside createMediaCluster of MediaClusterController.");
        var medias = mediaFacade.getMediasByRelation(mediaRelationData);
        var response = new ServiceResponseData();
        response.setStatus(ProcessStatus.SUCCESS);
        response.setData(medias);
        return response;
    }

}
