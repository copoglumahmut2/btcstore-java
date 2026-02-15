package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.SearchFacade;
import com.btc_store.service.constant.ServiceConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@RestController("backSearchControllerV1")
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.SEARCH)
@RequiredArgsConstructor
public class SearchController {

    protected final SearchFacade searchFacade;

    @PostMapping(ControllerMappings.PAGE + "/{itemType}")
    @Operation(summary = "Dynamic search.")
    public ServiceResponseData search(@Parameter(description = "Pagination page number")
                                      @PathVariable int page,
                                      @Parameter(description = "Search conditions")
                                      @PathVariable String itemType,
                                      @RequestBody String searchFormData,
                                      @Parameter(description = "IsoCode for validation message internalization")
                                      @RequestParam(required = false) String isoCode) {

        var pageable = PageRequest.of(page - 1, 20);
        var items = searchFacade.search(itemType, pageable, searchFormData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(items);
        return responseData;
    }

    @GetMapping("/all-item-models")
    @Operation(summary = "Return all models.")
    public ServiceResponseData getAllItemModels(@Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {

        var items = searchFacade.getAllItemModels();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(items);
        return responseData;
    }

    @GetMapping("/all-item-fields" + ControllerMappings.CODE)
    @Operation(summary = "Return all models.")
    public ServiceResponseData getAllItemFields(@Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode,
                                                @Parameter(description = "Class name")
                                                @PathVariable String code) {

        var items = searchFacade.getAllItemFields(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(items);
        return responseData;
    }

    @GetMapping("/all-item-fields-except-many" + ControllerMappings.CODE)
    @Operation(summary = "Return all fields on given item")
    public ServiceResponseData getAllItemFieldsForExcel(@Parameter(description = "IsoCode for validation message internalization")
                                                        @RequestParam(required = false) String isoCode,
                                                        @Parameter(description = "Class name")
                                                        @PathVariable String code) {

        var items = searchFacade.getAllItemFieldsExceptMany(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(items);
        return responseData;
    }

    @PostMapping(value = "/export-excel" + "/{itemType}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export excel as given param....")
    public ResponseEntity<Resource> exportExcel(@Parameter(description = "Item type")
                                                @PathVariable String itemType,
                                                @Parameter(description = "Search Form Data")
                                                @RequestBody String searchFormData,
                                                @Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {

        var exportExcelIs = searchFacade.exportExcel(itemType, searchFormData);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + itemType
                        + ServiceConstant.UNDERSCORE
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")) + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(exportExcelIs));

    }

    @PostMapping("/all-item-fields")
    @Operation(summary = "Return all models.")
    public ServiceResponseData getAllItemFields(@Parameter(description = "Items for fields getting")
                                                @Validated @RequestBody Set<String> items,
                                                @Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {

        var fields = searchFacade.getAllItemFields(items);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(fields);
        return responseData;
    }

    @PostMapping(ControllerMappings.QUERY_SEARCH + ControllerMappings.PAGE + "/{itemType}")
    @Operation(summary = "Dynamic Query search.")
    public ServiceResponseData searchByQuery(@Parameter(description = "Pagination page number")
                                             @PathVariable int page,
                                             @Parameter(description = "Search conditions")
                                             @PathVariable String itemType,
                                             @RequestBody String queryData,
                                             @Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {

        var pageable = PageRequest.of(page - 1, 20);
        var items = searchFacade.searchByQuery(itemType, pageable, queryData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(items);
        return responseData;
    }

    @GetMapping("/search-all" + "/{itemType}")
    @Operation(summary = "Return all fields on given item")
    public ServiceResponseData searchAll(@Parameter(description = "Item type")
                                         @PathVariable String itemType,
                                         @Parameter(description = "IsoCode for validation message internalization")
                                         @RequestParam(required = false) String isoCode) {

        var items = searchFacade.searchAll(itemType);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(items);
        return responseData;
    }


}

