package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.localize.LanguageData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.localize.LanguageModel;
import com.btc_store.facade.LanguageFacade;
import com.btc_store.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LanguageFacadeImpl implements LanguageFacade {

    private final ModelMapper modelMapper;
    private final SearchService searchService;

    @Override
    public List<LanguageData> getAllLanguages() {
        var languageModels = searchService.search(LanguageModel.class, Map.of(), SearchOperator.AND);
        return List.of(modelMapper.map(languageModels, LanguageData[].class));
    }
}
