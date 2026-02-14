package com.btc_store.security.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter @AllArgsConstructor
public class ErrorObject {
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("status_code")
    private int statusCode;
    private String timestamp;

    public ErrorObject(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
