package com.btc_store.facade;


import com.btc_store.domain.data.custom.LegalDocumentData;

public interface LegalDocumentFacade {

    LegalDocumentData getLegalDocumentByCode(String code);

    LegalDocumentData saveLegalDocument(LegalDocumentData legalDocumentData);

    void deleteLegalDocument(String code);
}
