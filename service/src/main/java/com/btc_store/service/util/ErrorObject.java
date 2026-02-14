package com.btc_store.service.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class ErrorObject implements Serializable {

    @JsonProperty("error_message")
    protected String errorMessage;
    @JsonProperty("status_code")
    protected int statusCode;
    protected String timestamp;
}
