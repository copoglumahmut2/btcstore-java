package com.btc_store.domain.model.custom;

import com.btc_store.domain.model.store.StoreMenuLinkItemModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class MenuLinkItemModel extends StoreMenuLinkItemModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
