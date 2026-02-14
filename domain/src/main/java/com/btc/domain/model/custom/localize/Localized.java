package com.btc.domain.model.custom.localize;

import com.btc.domain.model.store.localize.StoreLocalized;
import jakarta.persistence.Embeddable;
import java.io.Serial;

@Embeddable
public class Localized extends StoreLocalized {

    @Serial
    private static final long serialVersionUID = 1L;
}
