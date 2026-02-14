package com.btc.domain.model.custom.localize;

import com.btc.domain.model.store.localize.StoreLanguageModel;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
public class LanguageModel extends StoreLanguageModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
