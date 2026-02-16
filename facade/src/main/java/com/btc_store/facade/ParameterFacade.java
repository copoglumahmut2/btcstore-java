package com.btc_store.facade;

import com.btc_store.domain.data.custom.ParameterData;

import java.util.List;

public interface ParameterFacade {
    List<ParameterData> getAllParameters();
    ParameterData getParameterByCode(String code);
    ParameterData saveParameter(ParameterData parameterData);
    void deleteParameter(String code);
}
