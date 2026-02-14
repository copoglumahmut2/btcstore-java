package com.btc.domain.data.store.login;

import com.btc.domain.model.custom.SiteModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreLoginRequest {
    private String username;
    private String password;
    private SiteModel site;
    private boolean backoffice;
}
