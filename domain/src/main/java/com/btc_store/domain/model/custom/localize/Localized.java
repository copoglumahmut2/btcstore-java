package com.btc_store.domain.model.custom.localize;

import com.btc_store.domain.model.store.localize.StoreLocalized;
import jakarta.persistence.Embeddable;
import java.io.Serial;

@Embeddable
public class Localized extends StoreLocalized {

    @Serial
    private static final long serialVersionUID = 1L;
}
