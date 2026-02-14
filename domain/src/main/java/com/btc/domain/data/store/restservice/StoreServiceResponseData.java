package com.btc.domain.data.store.restservice;

import com.btc.domain.enums.ProcessStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StoreServiceResponseData {
    private ProcessStatus status;
    private String errorMessage;
    private String errorMessageDetail;
    private String errorLogUid;
    private String detail;
    private Object data;
    private String node;
    private String service;
    @JsonProperty(value = "bv")
    private String buildVersion;
    @JsonProperty("bt")
    private String buildTimestamp;
    @JsonProperty("ct")
    private String commitTimestamp;
    private boolean encryptedData;
}
