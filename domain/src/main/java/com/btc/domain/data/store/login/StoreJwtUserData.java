package com.btc.domain.data.store.login;

import com.btc.domain.model.custom.SiteModel;
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
