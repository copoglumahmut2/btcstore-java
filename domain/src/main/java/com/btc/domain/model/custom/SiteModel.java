package com.btc.domain.model.custom;

import com.btc.domain.model.store.StoreSiteModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import java.io.Serial;

@Entity
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SiteModel extends StoreSiteModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
