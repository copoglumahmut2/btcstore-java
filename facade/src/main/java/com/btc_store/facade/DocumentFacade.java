package com.btc_store.facade;

import com.btc_store.domain.data.custom.DocumentData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentFacade {
    List<DocumentData> getAllDocuments();
    DocumentData getDocumentByCode(String code);
    DocumentData saveDocument(DocumentData documentData, List<MultipartFile> mediaFiles);
    void deleteDocument(String code);
}
