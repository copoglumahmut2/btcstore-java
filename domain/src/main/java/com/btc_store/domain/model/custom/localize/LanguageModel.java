package com.btc_store.domain.model.custom.localize;

import com.btc_store.domain.model.store.localize.StoreLanguageModel;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
public class LanguageModel extends StoreLanguageModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
