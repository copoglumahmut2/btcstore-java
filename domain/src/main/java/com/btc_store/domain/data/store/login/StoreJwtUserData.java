package com.btc_store.domain.data.store.login;

import com.btc_store.domain.model.custom.SiteModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreJwtUserData {

    private String jwtId;
    private SiteModel site;
}
