package com.btc.domain.model.custom;


import com.btc.domain.model.store.StoreParameterModel;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
public class ParameterModel extends StoreParameterModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
