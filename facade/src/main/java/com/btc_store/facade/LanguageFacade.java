package com.btc_store.facade;

import com.btc_store.domain.data.custom.localize.LanguageData;

import java.util.List;

public interface LanguageFacade {

    List<LanguageData> getAllLanguages();
}
