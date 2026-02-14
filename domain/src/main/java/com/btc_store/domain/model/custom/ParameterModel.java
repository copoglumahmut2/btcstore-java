package com.btc_store.domain.model.custom;


import com.btc_store.domain.model.store.StoreParameterModel;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
public class ParameterModel extends StoreParameterModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
