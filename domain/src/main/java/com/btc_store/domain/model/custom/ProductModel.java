package com.btc_store.domain.model.custom;

import com.btc_store.domain.model.store.StoreProductModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ProductModel extends StoreProductModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
